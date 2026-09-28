import request from "@/utils/request";

//部门列表查询
export const queryAll = () => request.get("/depts");

