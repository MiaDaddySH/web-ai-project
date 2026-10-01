export const subjectOptions = [
  { value: 1, label: 'Java' },
  { value: 2, label: '前端' },
  { value: 3, label: '大数据' },
  { value: 4, label: 'Python' },
  { value: 5, label: 'Go' },
  { value: 6, label: '嵌入式' },
]

// 用于班级列表中显示学科名称
export function getSubjectName(value) {
  return subjectOptions.find(option => option.value === value)?.label
    ?? '未知学科'
}