const oppoPlantAgentProductSelList = r => require.ensure([], () => r(require('pages/tool/input/oppoPlantAgentProductSelList.vue')));

let routes = [
  {path: '/tool/sys/oppoPlantAgentProductSelList',component: oppoPlantAgentProductSelList,name: 'oppoPlantAgentProductSelList'}
];

export default routes;
