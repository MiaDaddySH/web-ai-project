import request from "@/utils/request";

//部门列表查询
export const queryAll = () => request.get("/depts");

//添加部门
export const addDept = (dept) => request.post("/depts",dept);

//根据id查询部门
export const queryById = (id) => request.get(`/depts/${id}`);

//修改部门
export const updateDept = (dept) => request.put("/depts",dept);

//根据id删除部门
export const deleteById = (id) => request.delete(`/depts?id=${id}`);
