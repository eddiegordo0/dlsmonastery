<template>
  <div>
    <head-tab active="officeRuleList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus"  v-permit="'sys:officeRule:edit'">{{$t('officeRuleList.add')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" >{{$t('officeRuleList.filter')}}</el-button>
        <span  v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('officeRuleList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData"  :label-width="formLabelWidth">
              <el-form-item :label="$t('officeRuleList.name')">
                <el-input v-model="formData.name" auto-complete="off" :placeholder="$t('officeRuleList.likeSearch')"></el-input>
              </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('officeRuleList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('officeRuleList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="id" :label="$t('officeRuleList.id')" sortable width="150"></el-table-column>
        <el-table-column prop="name" :label="$t('officeRuleList.name')" ></el-table-column>
        <el-table-column prop="code" :label="$t('officeRuleList.code')" ></el-table-column>
        <el-table-column prop="parentName" :label="$t('officeRuleList.parentName')" ></el-table-column>
        <el-table-column prop="hasPoint" :label="$t('officeRuleList.hasPoint')" width="100">
          <template scope="scope">
            <el-tag :type="scope.row.hasPoint? 'primary' : 'danger'">{{scope.row.hasPoint | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" :label="$t('officeRuleList.remarks')"></el-table-column>
        <el-table-column fixed="right" :label="$t('officeRuleList.operation')" width="140">
          <template scope="scope">
            <el-button size="small" @click.native="itemAction(scope.row.id,'edit')"  v-permit="'sys:officeRule:edit'">{{$t('officeRuleList.edit')}}</el-button>
            <el-button size="small" @click.native="itemAction(scope.row.id,'delete')"  v-permit="'sys:officeRule:delete'">{{$t('officeRuleList.delete')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        page:{},
        searchText:"",
        formData:{
            extra:{}
        },
        initPromise:{},
        formLabelWidth: '25%',
        pageLoading: false,
        formVisible: false,
      };
    },
    methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },
      pageRequest() {
        this.pageLoading = true;
        this.setSearchText();
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/basic/sys/officeRule?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.order=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },itemAdd(){
        this.$router.push({ name: 'officeRuleForm'})
      },itemAction:function(id,action){
        if(action=="edit") {
          this.$router.push({ name: 'officeRuleForm', query: { id: id }})
        } else if(action=="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
          axios.get('/api/basic/sys/officeRule/delete',{params:{id:id}}).then((response) =>{
            this.$message(response.data.message);
            this.pageRequest();
          });
        }).catch(()=>{});
        }
      }
    },created () {
      var that = this;
      that.pageHeight = 0.74*window.innerHeight;
      this.initPromise=axios.get('/api/basic/sys/officeRule/getQuery').then((response) =>{
        that.formData=response.data;
    });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

