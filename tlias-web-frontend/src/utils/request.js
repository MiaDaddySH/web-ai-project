import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";

const request = axios.create({
  baseURL: "/api",
  timeout: 600000,
});

request.interceptors.response.use(
  (response) => {//成功响应拦截器

    return response.data;
  },
  (error) => {
    //如果response响应码为401时，
    if (error.response.status === 401) {
      //提示信息
      ElMessage.error("登录信息已过期，请重新登录");
      //页面就应该跳转到登录页面
      router.push("/login");
    } else {
      ElMessage.error(error.message);
    }
    return Promise.reject(error);
  }
);
request.interceptors.request.use(
  //从localstorage中获取token
  (config) => {//成功回调
    const loginUser = JSON.parse(localStorage.getItem("loginUser"));
    if (loginUser && loginUser.token) {
      config.headers.token = loginUser.token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
 )

export default request;