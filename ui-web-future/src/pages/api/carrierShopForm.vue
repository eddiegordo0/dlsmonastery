<template>
  <div>
    <head-tab active="carrierShopForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-form-item :label="$t('carrierShopForm.name')" prop="name">
          <el-input v-model="inputForm.name"></el-input>
        </el-form-item>
        <el-form-item :label="$t('carrierShopForm.code')" prop="code">
          <el-input v-model.number="inputForm.code"></el-input>
        </el-form-item>
        <el-form-item :label="$t('carrierShopForm.type')" prop="type">
          <el-select v-model="inputForm.type" clearable filterable :placeholder="$t('carrierShopForm.selectGroup')">
            <el-option v-for="type in inputForm.extra.typeList" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('carrierShopForm.remarks')" prop="remarks">
          <el-input v-model="inputForm.remarks"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('carrierShopForm.save')}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
  import ProductSelect from "components/future/product-select.vue"
  export default{
    components:{
      ProductSelect
    },
    data(){
      return this.getData()
    },
    methods:{
      getData() {
        return{
          isCreate:this.$route.query.id==null,
          submitDisabled:false,
          inputForm:{
            extra:{}
          },
          rules: {
            name: [{ required: true, message: this.$t('carrierShopForm.prerequisiteMessage')}],
            type: [{ required: true, message: this.$t('carrierShopForm.prerequisiteMessage')}],
          }
        }
      },
      formSubmit(){
        var that = this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            axios.post('/api/ws/future/api/carrierShop/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(this.isCreate){
                Object.assign(this.$data, this.getData());
                this.initPage();
              }else{
                util.closeAndBackToPage(this.$router,'carrierShopList')
              }
            }).catch(function () {
              that.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },initPage(){
        axios.get('/api/ws/future/api/carrierShop/getForm').then((response)=>{
          this.inputForm = response.data;
          axios.get('/api/ws/future/api/carrierShop/findOne',{params: {id:this.$route.query.id}}).then((response)=>{
            util.copyValue(response.data,this.inputForm);
          });
        });
      }
    },created () {
      this.initPage();
    }
  }
</script>
