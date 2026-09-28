import axios from "axios";

const request = axios.create({
  baseURL: "/api",
  timeout: 600000,
});

request.interceptors.response.use(
  (response) => {//成功响应拦截器
    return response.data;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;