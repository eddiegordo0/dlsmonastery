<template>
  <div>
    <head-tab active="menuForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px"  class="form input-form">
        <el-row :gutter = "20">
          <el-col :span="10">
            <el-form-item :label="$t('menuForm.menuCategory')" prop="menuCategoryId">
            <el-select v-model="inputForm.menuCategoryId" filterable :placeholder="$t('menuForm.selectGroup')">
              <el-option v-for="category in inputForm.extra.menuCategoryList" :key="category.id" :label="category.name" :value="category.id"></el-option>
            </el-select>
            </el-form-item>
            <el-form-item :label="$t('menuForm.name')" prop="name">
              <el-input v-model="inputForm.name"></el-input>
            </el-form-item>
            <el-form-item :label="$t('menuForm.code')" prop="code">
              <el-input v-model="inputForm.code"></el-input>
            </el-form-item>
            <el-form-item :label="$t('menuForm.sort')" prop="sort">
              <el-input v-model="inputForm.sort"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item :label="$t('menuForm.mobile')" prop="mobile">
              <bool-radio-group v-model="inputForm.mobile"></bool-radio-group>
            </el-form-item>
            <el-form-item :label="$t('menuForm.visible')" prop="visible">
              <bool-radio-group v-model="inputForm.visible"></bool-radio-group>
            </el-form-item>
            <el-form-item :label="$t('menuForm.remarks')" prop="remarks">
              <el-input v-model="inputForm.remarks"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('menuForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
  import boolRadioGroup from 'components/common/bool-radio-group'
  export default{
    components:{
      boolRadioGroup
    },
      data:function () {
        return this.getData();
      },
      methods:{
        getData(){
          return{
            isCreate:this.$route.query.id==null,
            submitDisabled:false,
            inputForm:{
              extra:{}
            },
            rules: {
              menuCategoryId: [{ required: true, message: this.$t('menuForm.prerequisiteMessage')}],
              name: [{ required: true, message: this.$t('menuForm.prerequisiteMessage')}],
              menuCode: [{ required: true, message: this.$t('menuForm.prerequisiteMessage')}],
              sort: [{ required: true, message: this.$t('menuForm.prerequisiteMessage')}],
            }
          }
        },
        formSubmit(){
          var that = this ;
          this.submitDisabled = true;
          var form = this.$refs["inputForm"];
          form.validate((valid) => {
            if (valid) {
              axios.post('/api/basic/sys/menu/save',qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
                this.$message(response.data.message);
                if(this.isCreate){
                  Object.assign(this.$data,this.getData());
                  this.initPage();
                }else{
                    util.closeAndBackToPage(this.$router,"menuList")
                }
              }).catch(function () {
                that.submitDisabled = false;
              });
            }else{
              this.submitDisabled = false;
            }
          })
        },initPage(){
            axios.get('/api/basic/sys/menu/getForm').then((response)=>{
              this.inputForm = response.data;
              axios.get('/api/basic/sys/menu/findOne',{params: {id:this.$route.query.id}}).then((response)=>{
                util.copyValue(response.data,this.inputForm);
              })
            })
        }
      },created(){
      this.initPage();
    }
    }
</script>
