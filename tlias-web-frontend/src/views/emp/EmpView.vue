<script setup>
// 页面组件：负责查询、列表、分页、选中与删除。
// 新增和编辑表单由 EmployeeFormDialog.vue 负责。
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, EditPen } from '@element-plus/icons-vue'
import EmployeeFormDialog from '@/components/emp/EmployeeFormDialog.vue'
import { queryPageApi, queryInfoApi, deleteApi } from '@/api/emp'
import { queryAll as queryAllDeptApi } from '@/api/dept'

// 职位、性别是固定选项：列表用来显示名称，也传给对话框生成下拉选项。
// 不会被修改的数据使用普通数组即可，不需要 ref。
const jobs = [
  { name: '班主任', value: 1 },
  { name: '讲师', value: 2 },
  { name: '学工主管', value: 3 },
  { name: '教研主管', value: 4 },
  { name: '咨询师', value: 5 },
  { name: '其他', value: 6 },
]
const genders = [
  { name: '男', value: 1 },
  { name: '女', value: 2 },
]

// 查询条件与接口返回的列表数据。
// ref 在脚本中通过 .value 读取或修改；在模板中会自动解包。
const searchEmp = ref({ name: '', gender: '', date: [] })
const empList = ref([])
const depts = ref([])

// 分页状态：当前页、每页数量、符合条件的总记录数。
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表格勾选状态。empTableRef 指向 el-table 组件，用于调用 clearSelection()。
const selectedIds = ref([])
const empTableRef = ref(null)

// 对话框状态：null 代表新增；员工对象代表编辑。
const dialogVisible = ref(false)
const editingEmployee = ref(null)

// 接口返回数字代码；表格需要展示对应的中文名称。
// 找不到匹配值时显示 '-'，避免把未知代码显示成错误的职位或性别。
const jobName = value => jobs.find(job => job.value === Number(value))?.name ?? '-'
const genderName = value => genders.find(gender => gender.value === Number(value))?.name ?? '-'

// 根据当前的查询条件与分页状态加载员工列表。
const search = async () => {
  const { name, gender, date } = searchEmp.value

  // 日期控件保存的是 [开始日期, 结束日期]；接口分别接收 begin 和 end。
  // 清空日期时 date 可能是 null，因此先用 ?? [] 兜底。
  const [begin = '', end = ''] = date ?? []

  try {
    const result = await queryPageApi(
      name, gender, begin, end, currentPage.value, pageSize.value
    )
    if (result.code !== 1) {
      ElMessage.error(result.msg || '查询员工失败')
      return
    }
    // 查询成功后更新响应式状态，表格和分页条会随之重新渲染。
    empList.value = result.data.rows ?? []
    total.value = result.data.total ?? 0
  } catch {
    // 这里处理请求抛出的异常；上面的 result.code 处理接口返回的业务错误。
    ElMessage.error('查询员工失败')
  }
}

// 使用新条件查询时先回到第一页，避免停留在旧条件的较后页。
const applySearch = () => {
  currentPage.value = 1
  search()
}
// 清空查询条件后立即重新查询第一页。
const clear = () => {
  searchEmp.value = { name: '', gender: '', date: [] }
  applySearch()
}

// 部门列表只供表单中的“所属部门”下拉框使用。
const queryAllDepts = async () => {
  try {
    const result = await queryAllDeptApi()
    if (result.code === 1) {
      depts.value = result.data ?? []
    } else {
      ElMessage.error(result.msg || '加载部门失败')
    }
  } catch {
    ElMessage.error('加载部门失败')
  }
}

// 组件首次显示时，分别加载员工列表和部门选项。
onMounted(() => {
  search()
  queryAllDepts()
})
// 新增：不给对话框传员工对象；对话框打开时会创建空表单。
const addEmp = () => {
  editingEmployee.value = null
  dialogVisible.value = true
}
// 编辑：先根据 id 获取完整员工资料，再打开对话框并传入资料。
const edit = async (id) => {
  try {
    const result = await queryInfoApi(id)
    if (result.code !== 1) {
      ElMessage.error(result.msg || '查询员工失败')
      return
    }
    editingEmployee.value = result.data
    dialogVisible.value = true
  } catch {
    ElMessage.error('查询员工失败')
  }
}
// el-table 返回选中的“员工对象数组”；删除接口需要的是 id 数组。
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}
// 统一处理单条删除和批量删除的确认步骤。
// 用户取消会使 confirm 的 Promise 被拒绝，这里返回 false，不再调用删除接口。
const confirmDelete = async (message) => {
  try {
    await ElMessageBox.confirm(message, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })
    return true
  } catch {
    ElMessage.info('您已取消删除')
    return false
  }
}
// 两种删除共用同一段请求与刷新逻辑。
const deleteEmployees = async (ids) => {
  try {
    // 沿用原接口：单条传 id，批量传 id 数组。
    const result = await deleteApi(ids)
    if (result.code !== 1) {
      ElMessage.error(result.msg || '删除失败')
      return
    }
    ElMessage.success('删除成功')
    selectedIds.value = []
    empTableRef.value?.clearSelection()
    await search()
  } catch {
    ElMessage.error('删除失败')
  }
}
// 单条删除：确认后传入一个 id。
const deleteById = async (id) => {
  if (await confirmDelete('您确认删除该员工吗？')) {
    await deleteEmployees(id)
  }
}
// 批量删除：先检查有没有选中员工，再确认并传入 id 数组。
const deleteByIds = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.info('您没有选择任何要删除的数据')
    return
  }
  if (await confirmDelete('您确认删除选中的员工吗？')) {
    await deleteEmployees([...selectedIds.value])
  }
}
</script>

<template>
  <h1>员工管理</h1>

  <!-- 查询区：v-model 将输入值写入 searchEmp；提交时回到第一页查询。 -->
  <div class="container">
    <el-form :inline="true" :model="searchEmp" @submit.prevent="applySearch">
      <el-form-item label="姓名">
        <el-input v-model="searchEmp.name" placeholder="请输入员工姓名" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="searchEmp.gender" placeholder="请选择">
          <el-option
            v-for="gender in genders"
            :key="gender.value"
            :label="gender.name"
            :value="String(gender.value)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="入职时间">
        <el-date-picker
          v-model="searchEmp.date"
          type="daterange"
          range-separator="到"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">查询</el-button>
        <el-button type="info" @click="clear">清空</el-button>
      </el-form-item>
    </el-form>
  </div>

  <!-- 操作区：新增打开对话框；批量删除使用 selectedIds。 -->
  <div class="container">
    <el-button type="primary" @click="addEmp">+ 新增员工</el-button>
    <el-button type="danger" @click="deleteByIds">- 批量删除</el-button>
  </div>

  <!-- 列表区：选择列产生 selection-change；每行按钮使用该行的 id。 -->
  <div class="container">
    <el-table
      ref="empTableRef"
      :data="empList"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="name" label="姓名" width="120" align="center" />
      <el-table-column label="性别" width="120" align="center">
        <template #default="{ row }">{{ genderName(row.gender) }}</template>
      </el-table-column>
      <el-table-column label="头像" width="120" align="center">
        <template #default="{ row }">
          <img v-if="row.image" :src="row.image" alt="员工头像" class="table-avatar" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="所属部门" width="120" align="center" />
      <el-table-column label="职位" width="120" align="center">
        <template #default="{ row }">{{ jobName(row.job) }}</template>
      </el-table-column>
      <el-table-column prop="entryDate" label="入职日期" width="180" align="center" />
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
      @size-change="search"
      @current-change="search"
    />
  </div>

  <!-- 父组件向子组件传数据（props），子组件用事件通知父组件。
       v-model:visible 对应 visible / update:visible；
       saved 表示新增或修改成功，需要重新查询列表。 -->
  <EmployeeFormDialog
    v-model:visible="dialogVisible"
    :employee-data="editingEmployee"
    :departments="depts"
    :jobs="jobs"
    :genders="genders"
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
