<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:product:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:product:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:product:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:product:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键id" align="center" prop="id" /> -->
      <el-table-column label="商品名称" align="center" prop="name" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:product:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script >
import { listProduct, getProduct, delProduct, addProduct, updateProduct } from "@/api/system/product";

export default {
  name: "Product",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      //表单规则
      rules: {},
      title: "",
      productList: [],
      single: false,
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
/** 查询商品列表 */
 getList() {
    this.loading = true;
  listProduct(this.queryParams).then(response => {
    this.productList = response.rows;
    this.total = response.total;
    this.loading = false;
  });
},

// 取消按钮
 cancel() {
  this.open = false;
  this.reset();
},

// 表单重置
//  reset() {
//   form.value = {
//     id: null,
//     name: null
//   };
//   proxy.resetForm("productRef");
// },
// 表单重置
reset() {
  this.form = {
    name: undefined,
  };
  this.resetForm("form");
},

/** 搜索按钮操作 */
 handleQuery() {
  this.queryParams.pageNum = 1;
  this.getList();
},

/** 重置按钮操作 */
 resetQuery() {
  this.resetForm("queryRef");
  this.handleQuery();
},

// 多选框选中数据
 handleSelectionChange(selection) {
  this.ids = selection.map(item => item.id);
  this.single = selection.length != 1;
  this.multiple = !selection.length;
},

/** 新增按钮操作 */
 handleAdd() {
  this.reset();
  this.open = true;
  this.title = "添加商品";
},

/** 修改按钮操作 */
 handleUpdate(row) {
  console.log(row,"row");
  this.reset();
  const id = row.id || this.ids
  getProduct(id).then(response => {
    this.form = response.data;
    this.open = true;
    this.title = "修改商品";
  });
},

/** 提交按钮 */
 submitForm() {
  this.$refs["form"].validate(valid => {
    if (valid) {
      if (this.form.id != null) {
        updateProduct(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.open = false;
          this.getList();
        });
      } else {
        addProduct(this.form).then(response => {
          this.$modal.msgSuccess("新增成功");
          this.open = false;
          this.getList();
        });
      }
    }
  });
},

/** 删除按钮操作 */
handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除商品编号为"' + ids + '"的数据项？').then(function() {
        return delProduct(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

/** 导出按钮操作 */
handleExport() {
    this.download('system/product/export', {
      ...this.queryParams
    }, `product_${new Date().getTime()}.xlsx`)
  }
}
};
</script>
