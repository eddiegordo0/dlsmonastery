<template>
  <div>
    <head-tab active="storeInventoryReport"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="formVisible = true" icon="search" v-permit="'crm:storeInventoryReport:view'">过滤</el-button>
        <el-dropdown @command="exportData">
            <el-button type="primary"  v-permit="'crm:storeInventoryReport:view'">导出 <i class="el-icon-caret-bottom el-icon--right"></i></el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="按合计">按合计导出</el-dropdown-item>
              <el-dropdown-item command="按串码">按串码导出</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
        <el-button type="primary" @click="search()">刷新</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" title="过滤" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData">
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item label="日期" :label-width="formLabelWidth">
                <date-picker v-model="formData.date"></date-picker>
              </el-form-item>
              <el-form-item label="打分型号" :label-width="formLabelWidth">
                <el-select v-model="formData.scoreType" clearable filterable placeholder="请选择">
                  <el-option v-for="(key,value) in formData.extra.boolMap" :key="key" :label="value | bool2str" :value="key"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="仓库名" :label-width="formLabelWidth">
                <depot-select v-model="formData.depotId"  category="store"></depot-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">确定</el-button>
        </div>
      </search-dialog>
      <el-table :data="depotStoreList"  style="margin-top:5px;" v-loading="pageLoading" element-loading-text="加载中"  @row-click="storeDetail" stripe border>
        <el-table-column column-key="areaId" prop="areaName" label="办事处" sortable></el-table-column>
        <el-table-column column-key="t1.officeId" prop="officeName" label="业务单元" sortable></el-table-column>
        <el-table-column column-key="t1.name" prop="depotName" label="仓库" sortable></el-table-column>
        <el-table-column prop="qty" :label="'数量'+sum" sortable></el-table-column>
        <el-table-column prop="percentage" label="占比(%)"></el-table-column>
      </el-table>
    </div>
    <div>
      <el-dialog title="详细" :visible.sync="detailVisible">
        <div style="width:100%;height:50px;text-align:center;font-size:20px">货品详情</div>
        <el-table :data="productDetail">
          <el-table-column property="productName" label="货品" width="400"></el-table-column>
          <el-table-column property="qty" label="串码" ></el-table-column>
        </el-table>
      </el-dialog>
    </div>
  </div>
</template>
<script>
  import productTypeSelect from 'components/future/product-type-select'
  import depotSelect from 'components/future/depot-select'
  export default {
    components:{
      productTypeSelect,
      depotSelect
    },
    data() {
      return {
        depotStoreList:[],
        searchText:"",
        formData:{
          extra:{},
        },
        sum:"",
        formLabelWidth: '120px',
        formVisible: false,
        detailVisible:false,
        pageLoading: false,
        productDetail:[],
      };
    },
    methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },
      initPage() {
        this.pageLoading = true;
        this.setSearchText();
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/ws/future/basic/depotStore/storeReport?'+qs.stringify(submitData)).then((response) => {
          this.depotStoreList = response.data.list;
          this.sum=response.data.sum;
          this.pageLoading = false;
        })
      },search() {
        this.formVisible = false;
        this.initPage();
      },storeDetail(row, event, column){
        this.formData.isDetail=true;
        this.formData.depotId=row.depotId;
        axios.post('/api/ws/future/basic/depotStore/storeReportDetail',qs.stringify(util.deleteExtra(this.formData))).then((response) => {
          let productList=response.data;
          let productDetail=[];
          if(productList){
            for(let key in productList){
              productDetail.push({productName:key,qty:productList[key]})
            }
            this.productDetail=productDetail;
          }
          this.detailVisible=true;
        })
      },exportData(command) {
        if(command ==='按合计') {
          util.confirmBeforeExportData(this).then(() => {
            window.location.href='/api/ws/future/basic/depotStore/export?'+qs.stringify(util.deleteExtra(this.formData));
          }).catch(()=>{});
        }
        if (command === '按串码'){
          window.location.href='/api/ws/future/basic/depotStore/exportDetail?'+qs.stringify(util.deleteExtra(this.formData));
        }
      }
    },created () {
      axios.get('/api/ws/future/basic/depotStore/getReportQuery').then((response) => {
        this.formData = response.data;
        this.formData.scoreType=this.formData.scoreType?"1":"0";
        this.initPage();
      })
    }
  };
</script>

