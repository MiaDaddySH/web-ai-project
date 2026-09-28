import axios from "axios";

const request = axios.create({
  baseURL: "http://127.0.0.1:4523/m1/8887930-8686664-default",
  // baseURL: "http://localhost:8080",
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