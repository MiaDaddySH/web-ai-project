<script setup>
import { nextTick, onMounted, ref  } from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus'
import {queryAll, addDept, queryById, updateDept, deleteById} from '@/api/dept';

const isSuccess = (response) => response.code === 1

const deptList = ref([]);
const showDialog = ref(false)
const dept = ref({name: ''})
const deptFormRef = ref(null)

const formTitle = ref('')

// 表单验证规则
const formRules = ref({ 
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
  ]
})
const search = async () => {
  try {
    const response = await queryAll()
    if (isSuccess(response)) {
      deptList.value = response.data
    } else {
      ElMessage.error(response.msg || '查询部门失败')
    }
  } catch {
    ElMessage.error('查询部门失败')
  }
}

onMounted(search);

const openDialog = async (data) => {
  dept.value = data
  showDialog.value = true

  await nextTick()
  deptFormRef.value?.clearValidate()
}

const add = () => {
  openDialog({ name: '' })
}

const handleEdit = async (id) => {
  try {
    const response = await queryById(id)
    if (isSuccess(response)) {
      openDialog({ ...response.data })
    } else {
      ElMessage.error(response.msg || '查询部门失败')
    }
  } catch {
    ElMessage.error('查询部门失败')
  }
}
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('你确认删除该部门吗？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return // 用户取消，直接结束
  }

  try {
    const response = await deleteById(id)
    if (!isSuccess(response)) {
      ElMessage.error(response.msg || '删除失败')
      return
    }

    ElMessage.success('删除成功')
    await search()
  } catch {
    ElMessage.error('删除失败')
  }
}

const save = async () => {
  if (!deptFormRef.value) return

  try {
    await deptFormRef.value.validate()
  } catch {
    return // 校验错误会显示在表单项下方
  }

  try {
    const isEdit = dept.value.id != null
    const response = isEdit
      ? await updateDept(dept.value)
      : await addDept(dept.value)

    if (!isSuccess(response)) {
      ElMessage.error(response.msg || '保存失败')
      return
    }

    ElMessage.success('操作成功')
    showDialog.value = false
    await search()
  } catch {
    ElMessage.error('保存失败')
  }
}
</script>
<template>
  <div class="toolbar">
    <h1>部门管理</h1>
    <el-button type="primary" @click="add">+ 新增部门</el-button>
  </div>

  <el-table :data="deptList" border style="width: 100%">
    <el-table-column type="index" label="序号" width="100" align="center" />
    <el-table-column prop="name" label="部门名称" width="300" align="center" />
    <el-table-column prop="updateTime" label="最后修改时间" width="300" align="center" />
    <el-table-column fixed="right" label="操作" align="center">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.row.id)">修改</el-button>
        <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog
    v-model="showDialog"
    :title="dept.id != null ? '编辑部门' : '新增部门'"
    width="30%"
  >
    <el-form
      ref="deptFormRef"
      :model="dept"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="dept.name" autocomplete="off" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="showDialog = false">取消</el-button>
      <el-button type="primary" @click="save">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.toolbar h1 {
  margin: 0;
}
</style>