<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { subjectOptions } from '@/constants/subjects'
import { addClazzApi, updateClazzApi } from '@/api/clazz'

const props = defineProps({
  visible: { type: Boolean, required: true },
  clazzData: { type: Object, default: null },
  masters: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:visible', 'saved'])

const emptyClazz = () => ({
  name: '',
  room: '',
  beginDate: '',
  endDate: '',
  masterId: '',
  subject: '',
})

const clazz = ref(emptyClazz())
const clazzFormRef = ref(null)
const saving = ref(false)

const isEdit = computed(() => clazz.value.id != null)

// 打开弹窗时初始化表单。
// 新增：使用空数据；编辑：复制父组件传入的班级资料。
watch(
  [() => props.visible, () => props.clazzData],
  async ([visible, data]) => {
    if (!visible) return

    clazz.value = {
      ...emptyClazz(),
      name: data?.name ?? '',
      room: data?.room ?? '',
      beginDate: data?.beginDate ?? '',
      endDate: data?.endDate ?? '',
      masterId: data?.masterId ?? '',
      subject: data?.subject ?? '',
      ...(data?.id != null ? { id: data.id } : {}),
    }

    await clearValidation()
  },
  { immediate: true },
)

// 等待表单更新后，再清除上次的校验提示。
async function clearValidation() {
  await nextTick()
  clazzFormRef.value?.clearValidate()
}

// 用户主动关闭时，保存期间不允许关闭。
const close = () => {
  if (saving.value) return
  emit('update:visible', false)
}

const rules = {
  name: [
    { required: true, message: '请输入班级名称', trigger: 'blur' },
    {
      min: 2,
      max: 20,
      message: '班级名称长度应在2到20个字符之间',
      trigger: 'blur',
    },
  ],
  subject: [
    { required: true, message: '请选择学科', trigger: 'change' },
  ],
  beginDate: [
    { required: true, message: '请选择开课时间', trigger: 'change' },
  ],
  endDate: [
    { required: true, message: '请选择结课时间', trigger: 'change' },
  ],
}

// 保存流程：防重复提交 → 校验 → 新增或修改 → 通知父组件。
const save = async () => {
  if (!clazzFormRef.value || saving.value) return

  saving.value = true

  try {
    try {
      await clazzFormRef.value.validate()
    } catch {
      // 校验失败时，字段下方会显示提示。
      return
    }

    // 班级没有员工的 exprList，不需要处理工作经历。
    const payload = {
      ...clazz.value,
      // 班主任未选择时传 null，避免数字字段收到空字符串。
      masterId: clazz.value.masterId === '' ? null : clazz.value.masterId,
    }

    const result = isEdit.value
      ? await updateClazzApi(payload)
      : await addClazzApi(payload)

    if (result.code !== 1) {
      ElMessage.error(result.msg || '保存班级失败')
      return
    }

    ElMessage.success('保存成功')

    // 保存成功后主动关闭，并通知父组件刷新列表。
    emit('update:visible', false)
    emit('saved')
  } catch {
    ElMessage.error('保存班级失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog
  :model-value="visible"
  :title="isEdit ? '修改班级' : '新增班级'"
  :close-on-click-modal="!saving"
  :close-on-press-escape="!saving"
  :show-close="!saving"
  @update:model-value="close"
  @open="clearValidation"
  >
    <!-- model 是表单数据，rules 是校验规则，ref 可在脚本中调用表单方法。 -->
    <el-form ref="clazzFormRef" :model="clazz" :rules="rules" label-width="80px">
      <el-form-item label="班级名称" prop="name">
        <el-input v-model="clazz.name" placeholder="请输入班级名称，如：上海JavaEE就业100期" />
      </el-form-item>

      <el-form-item label="班级教室">
        <el-input v-model="clazz.room" placeholder="请填写班级教室" />
      </el-form-item>

      <el-form-item label="开课时间" prop="beginDate">
        <el-date-picker
          v-model="clazz.beginDate"
          type="date"
          placeholder="请选择开课时间"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="结课时间" prop="endDate">
        <el-date-picker
          v-model="clazz.endDate"
          type="date"
          placeholder="请选择结课时间"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="班主任">
        <el-select v-model="clazz.masterId" placeholder="请选择" style="width: 100%">
          <el-option
            v-for="master in masters"
            :key="master.id"
            :label="master.name"
            :value="master.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学科" prop="subject">
        <el-select v-model="clazz.subject" placeholder="请选择学科" style="width: 100%">
          <el-option
            v-for="subject in subjectOptions"
            :key="subject.value"
            :label="subject.label"
            :value="subject.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    

    <!-- 保存期间禁用取消按钮，并在保存按钮上显示加载状态。 -->
    <template #footer>
      <el-button :disabled="saving" @click="close">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>


<style scoped>

</style>