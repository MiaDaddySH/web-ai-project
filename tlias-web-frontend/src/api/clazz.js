import request from '@/utils/request'

// 查询班级列表。
export const queryClazzesApi = (
  name = '',
  begin = undefined,
  end = undefined,
  page = 1,
  pageSize = 10,
) => {
  return request.get('/clazzs', {
    params: {
      name,
      begin,
      end,
      page,
      pageSize,
    },
  })
}

// 新增班级。
export const addClazzApi = (clazz) =>
  request.post('/clazzs', clazz)

// 根据 ID 查询班级。
export const queryByIdApi = (id) =>
  request.get(`/clazzs/${id}`)

// 修改班级。
export const updateClazzApi = (clazz) =>
  request.put('/clazzs', clazz)

// 删除班级：保留你现有的查询参数形式。
export const deleteByIdApi = (id) =>
  request.delete(`/clazzs/${id}`)