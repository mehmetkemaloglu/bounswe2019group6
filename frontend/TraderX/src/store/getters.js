const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  userSearchResult: state => state.search.userSearchResult,
  articleSearchResult: state => state.search.articleSearchResult,
  equipmentQueryResult: state => state.equipment.equipmentQueryResult,
  currencyResult: state => state.equipment.currencyResult,
  cryptoCurrencyResult: state => state.equipment.cryptoCurrencyResult,
  stockResult: state => state.equipment.stockResult,
  allPortfolios: state => state.equipment.allPortfolios,
  allEquipments: state => state.equipment.allEquipments,
  transaction: state => state.equipment.transaction,

  // TODO these are deprecated but we can keep useful ones
  roles: state => state.user.roles,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  introduction: state => state.user.introduction,

  permission_routes: state => state.permission.routes,
  errorLogs: state => state.errorLog.logs
}
export default getters
