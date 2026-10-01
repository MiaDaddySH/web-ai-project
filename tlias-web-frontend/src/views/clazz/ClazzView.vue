<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { EditPen, Delete } from '@element-plus/icons-vue'
import {
  queryClazzesApi,
  queryByIdApi,
  deleteByIdApi,
} from '@/api/clazz'
import { queryPageApi } from '@/api/emp'
import ClazzFormDialog from '../../components/clazz/ClazzFormDialog.vue'

const searchClazz = ref({ name: '', date: [] })
const clazzList = ref([])

// 分页状态。
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框状态：null 代表新增，班级对象代表编辑。
const dialogVisible = ref(false)
const editingClazz = ref(null)
const masters = ref([])

// 查询班主任选项。
// 按 queryPageApi(name, gender, begin, end, page, pageSize) 调用。
// 当前沿用员工分页接口，逐页加载，避免只显示第一页的员工。
const queryMasters = async () => {
  try {
    const employees = []
    const batchSize = 100
    let page = 1

    while (true) {
      const result = await queryPageApi(
        '', '', '', '', page, batchSize
      )

      if (result.code !== 1) {
        ElMessage.error(result.msg || '查询班主任失败')
        return
      }

      const rows = result.data.rows ?? []
      employees.push(...rows)

      if (
        rows.length === 0 ||
        employees.length >= Number(result.data.total)
      ) {
        break
      }

      page += 1
    }

    masters.value = employees
  } catch {
    ElMessage.error('查询班主任失败')
  }
}

// 根据查询条件与分页状态加载班级列表。
const search = async () => {
  const { name, date } = searchClazz.value
  const [begin = '', end = ''] = date ?? []

  try {
    const result = await queryClazzesApi(
      name,
      begin,
      end,
      currentPage.value,
      pageSize.value
    )

    if (result.code !== 1) {
      ElMessage.error(result.msg || '查询班级失败')
      return
    }

    clazzList.value = result.data.rows ?? []
    total.value = Number(result.data.total ?? 0)
  } catch {
    ElMessage.error('查询班级失败')
  }
}

// 修改每页数量时回到第一页。
watch(pageSize, () => {
  currentPage.value = 1
})

// 页码或每页数量变化后重新查询。
// 模板中不再额外绑定分页查询事件。
watch([currentPage, pageSize], () => {
  search()
})

// 应用新的查询条件。
const applySearch = () => {
  if (currentPage.value === 1) {
    search()
  } else {
    // 页码变化后由上面的 watch 负责查询。
    currentPage.value = 1
  }
}

const clear = () => {
  searchClazz.value = {
    name: '',
    date: [],
  }
  applySearch()
}

// 新增：传入 null，让弹窗初始化为空表单。
const addClazz = () => {
  editingClazz.value = null
  dialogVisible.value = true
}

// 编辑：查询完整班级资料，然后打开弹窗。
const edit = async (id) => {
  try {
    const result = await queryByIdApi(id)

    if (result.code !== 1) {
      ElMessage.error(result.msg || '查询班级失败')
      return
    }

    if (!result.data) {
      ElMessage.error('未查询到班级资料')
      return
    }

    // 原代码误写为 editingEmployee。
    editingClazz.value = result.data
    dialogVisible.value = true
  } catch {
    ElMessage.error('查询班级失败')
  }
}

// 单条删除：先确认，再调用班级删除接口。
const deleteById = async (id) => {
  try {
    await ElMessageBox.confirm(
      '您确认删除该班级吗？',
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
  } catch {
    // 用户取消或关闭确认框。
    return
  }

  try {
    const result = await deleteByIdApi(id)

    if (result.code !== 1) {
      ElMessage.error(result.msg || '删除班级失败')
      return
    }

    ElMessage.success('删除成功')

    // 当前页只有一条数据时，删除后返回上一页。
    if (clazzList.value.length === 1 && currentPage.value > 1) {
      currentPage.value -= 1
    } else {
      await search()
    }
  } catch {
    ElMessage.error('删除班级失败')
  }
}

onMounted(() => {
  search()
  queryMasters()
})
</script>

<template>
  <h1>班级管理</h1>
  <!-- 查询区：v-model 将输入值写入 searchEmp；提交时回到第一页查询。 -->
  <div class="container">
    <el-form :inline="true" :model="searchClazz" @submit.prevent="applySearch">
      <el-form-item label="班级名称">
        <el-input v-model="searchClazz.name" placeholder="请输入班级名称" />
      </el-form-item>
      <el-form-item label="结课时间">
        <el-date-picker
          v-model="searchClazz.date"
          type="daterange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button type="info" @click="clear">清空</el-button>
      </el-form-item>
    </el-form>
  </div>

  <!-- 操作区：新增打开对话框 -->
  <div class="container">
    <el-button type="primary" @click="addClazz">+ 新增班级</el-button>
  </div>

  <!-- 列表区：选择列产生 selection-change；每行按钮使用该行的 id。 -->
  <div class="container">
    <el-table
      :data="clazzList"
      border
      style="width: 100%"
    >
      
      <el-table-column type="index" label="序号" width="100" align="center" />
      <el-table-column prop="name" label="班级名称" width="300" align="center" />
      <el-table-column prop="room" label="班级教室" width="100" align="center" />
      <el-table-column prop="masterName" label="班主任" width="120" align="center" />

      <el-table-column prop="beginDate" label="开课时间" width="200" align="center" />
      <el-table-column prop="endDate" label="结课时间" width="200" align="center" />
      <el-table-column prop="status" label="状态" width="100" align="center" />
      <el-table-column prop="updateTime" label="最后操作时间" width="200" align="center" />
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="edit(row.id)">
            <el-icon><EditPen /></el-icon> 编辑
          </el-button>
          <el-button type="danger" size="small" @click="deleteById(row.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 分页区：页码或每页数量变化时重新读取员工列表。 -->
  <div class="container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20, 30, 50, 75, 100]"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    />
  </div>

  <!-- 父组件向子组件传数据（props），子组件用事件通知父组件。
       v-model:visible 对应 visible / update:visible；
       saved 表示新增或修改成功，需要重新查询列表。 -->
  <ClazzFormDialog
    v-model:visible="dialogVisible"
    :clazz-data="editingClazz"
    :masters="masters"
    @saved="search"
  />

</template>

<style scoped>
.container {
  margin: 10px 0;
}

.table-avatar {
  width: 30px;
  height: 30px;
  object-fit: cover;
}

</style>