<script setup>
import { ref, onMounted  } from 'vue';
import {queryAll} from '@/api/dept';

onMounted(() => {
  search();
});
  
const deptList = ref([]);
const search = async () => {
  const response = await queryAll();
  if(response.code) {//JS中隐式类型转换， Truthy 值
    deptList.value = response.data;
    console.log(deptList.value);
  }
};

// 编辑部门 - 根据ID查询回显数据
const handleEdit = (id) => {
  console.log(`Edit item with ID ${id}`);
  // 在这里实现编辑功能
};

// 删除部门 - 根据ID删除部门
const handleDelete = (id) => {
  console.log(`Delete item with ID ${id}`);
  // 在这里实现删除功能
};
</script>

<template>
  <h1>部门管理</h1>

  <!-- 按钮靠页面右侧显示 -->
  <el-button type="primary" @click="handleEdit" style="float: right;"> + 新增部门</el-button> <br><br>

  <el-table :data="deptList" border style="width: 100%;">
    <el-table-column type="index" label="序号" width="100" align="center"/>
    <el-table-column prop="name" label="部门名称" width="300" align="center"/>
    <el-table-column prop="updateTime" label="最后修改时间" width="300" align="center"/>
    <el-table-column fixed="right" label="操作" align="center">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.row.id)">修改</el-button>
        <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
  
</template>

<style scoped>

</style>
