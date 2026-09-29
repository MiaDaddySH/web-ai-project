<script setup>
// 这个组件只管理“新增/编辑员工”对话框：表单数据、校验、上传和保存。
// 员工列表与删除功能由父页面 EmpView.vue 管理。
import { computed, nextTick, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { addApi, updateApi } from '@/api/emp'

// props 是父组件传进来的数据。
// visible 控制显示；employeeData 为 null 时新增，有员工对象时编辑。
// 数组的默认值用函数返回，避免多个组件实例共享同一个数组。
const props = defineProps({
  visible: { type: Boolean, required: true },
  employeeData: { type: Object, default: null },
  departments: { type: Array, default: () => [] },
  jobs: { type: Array, default: () => [] },
  genders: { type: Array, default: () => [] },
})

// 子组件通过事件通知父组件：
// update:visible 用于关闭对话框；saved 表示保存成功，请父组件刷新列表。
const emit = defineEmits(['update:visible', 'saved'])

// employeeFormRef 指向下面的 el-form 组件，可调用 validate()/clearValidate()。
// saving 防止重复提交，也控制保存按钮的加载状态。
const employeeFormRef = ref(null)
const saving = ref(false)
// 工作经历的本地递增编号，供 v-for 的 :key 使用；它不是数据库 id。
let nextExprId = 0

// 工厂函数：每次调用都产生一个新的空对象和新的 exprList 数组。
// 因此再次打开“新增”时，不会继续使用上次填写过的表单对象。
const emptyEmployee = () => ({
  username: '',
  name: '',
  gender: '',
  phone: '',
  job: '',
  salary: '',
  deptId: '',
  entryDate: '',
  image: '',
  exprList: [],
})
// 表单的本地数据。输入框通过 v-model 修改 employee，而不是修改 props.employeeData。
const employee = ref(emptyEmployee())
// id 存在表示编辑；没有 id 表示新增。computed 会随 employee 的变化重新计算。
const isEdit = computed(() => employee.value.id != null)

// 校验规则按字段名组织，与 el-form-item 的 prop 一一对应。
// blur 是离开输入框时校验，change 是选择值变化时校验。
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度应在2到20个字符之间', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度应在2到10个字符之间', trigger: 'blur' },
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' },
  ],
}


// 只在对话框打开时初始化表单；关闭时不重建数据。
// 父组件先设置 employeeData，再把 visible 改成 true，因此这里能读到待编辑对象。
watch(() => props.visible, async (visible) => {
  if (!visible) return
  nextExprId = 0
  employee.value = props.employeeData
    ? {//编辑：用查到的员工数据填充表单
        ...emptyEmployee(),//提供所有字段的默认值
        ...props.employeeData,//用员工的实际数据覆盖默认值
        // ?? []：接口没返回工作经历时按空数组处理。
        // map：为每条经历创建新的表单对象，避免直接修改父组件传来的数组项。
        exprList: (props.employeeData.exprList ?? []).map(expr => ({
          ...expr, //通过展开语法（spread syntax）先把原工作经历的字段复制进新对象
          key: ++nextExprId, //给这条记录加一个供 Vue v-for 使用的本地唯一编号
          //如果开始和结束日期都有值，就组成 [开始日期, 结束日期]，供日期范围选择器使用；否则给空数组。
          exprDate: expr.begin && expr.end ? [expr.begin, expr.end] : [],
        })),
      }
    : emptyEmployee() // 新增：使用空表单
  // 等待 Vue 更新视图后再清理旧校验信息；?. 在表单尚未出现时避免报错。
  await nextTick()
  employeeFormRef.value?.clearValidate()//清空表单验证信息
})

// v-model:visible 的子组件写法：发出 update:visible(false)，让父组件关闭弹窗。
const close = () => emit('update:visible', false)

// 上传接口成功时把图片地址存进 employee.image，供预览与保存使用。
const handleAvatarSuccess = (response) => {
  if (response?.code === 1 && response.data) {
    employee.value.image = response.data
  } else {
    ElMessage.error(response?.msg || '头像上传失败')
  }
}

const handleAvatarError = () => ElMessage.error('头像上传失败')

// el-upload 上传之前调用；返回 false 会阻止不符合要求的文件上传。
const beforeAvatarUpload = (file) => {
  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    ElMessage.error('只支持 JPG 或 PNG 图片')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片不能超过 10 MB')
    return false
  }
  return true
}
// 每次添加一行工作经历，生成新的表单字段与稳定的列表 key。
const addExprItem = () => {
  employee.value.exprList.push({
    key: ++nextExprId,
    exprDate: [],
    company: '',
    job: '',
  })
}

// splice(index, 1)：从当前工作经历数组中移除指定位置的一项。
const delExprItem = (index) => employee.value.exprList.splice(index, 1)

// 保存流程：防重复提交 -> 校验 -> 组装接口数据 -> 新增或更新 -> 通知父组件。
const save = async () => {
  if (!employeeFormRef.value || saving.value) return

  // validate() 校验失败会拒绝 Promise；错误提示已显示在对应的表单项。
  try {
    await employeeFormRef.value.validate()
  } catch {
    return // 字段下方已经显示校验信息
  }

  // 把表单数据转换为接口需要的格式，避免修改正在编辑的 employee 对象。
  // { key, exprDate, ...item } 是解构：取出 UI 专用字段，其余字段收进 item。
  // 外层的 ...employee.value 和内层的 ...item 则是“展开”，用于创建新对象。
  const payload = {
    ...employee.value,
    exprList: employee.value.exprList.map(({ key, exprDate, ...item }) => ({
      ...item,
      begin: exprDate?.[0] ?? '',
      end: exprDate?.[1] ?? '',
    })),
  }

  saving.value = true
  try {
    // 编辑时 payload 包含员工 id；新增时没有 id。
    const result = isEdit.value ? await updateApi(payload) : await addApi(payload)
    if (result.code !== 1) {
      ElMessage.error(result.msg || '保存员工失败')
      return
    }
    ElMessage.success('保存成功')
    close()
    // 通知父组件重新查询员工列表；本组件不直接管理表格。
    emit('saved')
  } catch {
    ElMessage.error('保存员工失败')
  } finally {
    // 无论成功、业务失败还是网络异常，都恢复按钮状态。
    saving.value = false
  }
}
</script>

<template>
  <!-- model-value 接收父组件的 visible；关闭按钮/遮罩触发 update:model-value。 -->
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '修改员工' : '新增员工'"
    @update:model-value="emit('update:visible', $event)"
  >
    <!-- model 是表单数据，rules 是校验规则，ref 可在脚本中调用表单方法。 -->
    <el-form ref="employeeFormRef" :model="employee" :rules="rules" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="employee.username" placeholder="请输入员工用户名，2-20个字" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="employee.name" placeholder="请输入员工姓名，2-10个字" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 性别选项来自父组件；v-model 记录被选中的 value。 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="employee.gender" placeholder="请选择性别" style="width: 100%">
              <el-option v-for="gender in genders" :key="gender.value" :label="gender.name" :value="gender.value"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="employee.phone" placeholder="请输入员工手机号" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 职位、薪资：编辑时由本地 employee 对象回显。 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职位">
            <el-select v-model="employee.job" placeholder="请选择职位" style="width: 100%">
              <el-option
                v-for="job in jobs"
                :key="job.value"
                :label="job.name"
                :value="job.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="薪资">
            <el-input v-model="employee.salary" placeholder="请输入员工薪资" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 部门来自父页面加载的列表；日期通过 value-format 保存为字符串。 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所属部门">
            <el-select v-model="employee.deptId" placeholder="请选择部门" style="width: 100%">
              <el-option
                v-for="dept in departments"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入职日期">
            <el-date-picker
              v-model="employee.entryDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- 上传成功后 employee.image 变为图片地址，显示预览图。 -->
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="/api/upload"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="employee.image" :src="employee.image" alt="员工头像" class="avatar-preview" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <!-- 点击后往 employee.exprList 加入一条本地工作经历。 -->
      <el-form-item label="工作经历">
        <el-button type="success" size="small" @click="addExprItem">+ 添加工作经历</el-button>
      </el-form-item>

      <!-- 每条经历独占一行；expr.key 保证添加/删除后各行有稳定标识。 -->
      <el-row v-for="(expr, index) in employee.exprList" :key="expr.key" :gutter="3">
        <el-col :span="10">
          <el-form-item size="small" label="时间" label-width="80px">
            <el-date-picker
              v-model="expr.exprDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item size="small" label="公司" label-width="60px">
            <el-input v-model="expr.company" placeholder="请输入公司名称" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item size="small" label="职位" label-width="60px">
            <el-input v-model="expr.job" placeholder="请输入职位" />
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item size="small" label-width="0px">
            <el-button type="danger" @click="delExprItem(index)">删除</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 保存期间禁用取消按钮，并在保存按钮上显示加载状态。 -->
    <template #footer>
      <el-button :disabled="saving" @click="close">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.avatar-preview {
  width: 78px;
  height: 78px;
  display: block;
  object-fit: cover;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  overflow: hidden;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  width: 78px;
  height: 78px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
}
</style>
