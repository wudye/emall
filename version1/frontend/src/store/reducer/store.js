import { configureStore } from "@reduxjs/toolkit";
import { productsReducer } from "./productsReducer";
import { errorReducer } from "./errorReducer";
import { cartReducer } from "./cartReducer";
import { authReducer } from "./authReducer";
import { parseJwt } from "../../utils/jwtDecoder";

const cartItems = localStorage.getItem('cartItems')
  ? JSON.parse(localStorage.getItem('cartItems'))
  : []

const userFromStorage = localStorage.getItem('auth')
  ? JSON.parse(localStorage.getItem('auth'))
  : null

let decodedUser = null
if (userFromStorage && userFromStorage.token ) {
  const decoded = parseJwt(userFromStorage.token);
  decodedUser = {
    ...userFromStorage,
    userId: decoded.userId,
    email: decoded.email,
  }
}


/* This code defines the initial state for your Redux store. The initialState object organizes your application's state into two main sections: auth and carts.

The auth property holds authentication-related data. Specifically, it contains a user field, which is set to decodedUser. This value is typically derived from user information stored in local storage and may include details like user ID and email, especially if the user is logged in.
The carts property manages shopping cart data. It contains a cart field, which is initialized with cartItems. These items are also loaded from local storage, ensuring that the user's cart persists across sessions.
Structuring your state this way makes it easier to manage and update different parts of your application independently. A common 'gotcha' is forgetting to keep the shape of your initial state consistent with what your reducers expect, which can lead to bugs or unexpected behavior. */

/* Example of a 'gotcha':

If your reducer expects the state to have a structure like { auth: { user: null }, carts: { cart: [] } }, but your initialState is defined as { user: null, cart: [] }, your application may not function correctly because the state shape does not match what the reducer is designed to handle. Always ensure that the initial state aligns with the expected structure in your reducers.
*/

const  initialState = {
  auth: {user: decodedUser },
  carts: {cart: cartItems},
}


const store = configureStore({
  reducer: {
       products: productsReducer,
    error: errorReducer,
    carts: cartReducer,
    auth: authReducer,
  },
  
  preloadedState: initialState,
})
export default store



/* the code in your selection is for creating a Redux store, which is typically written as an object passed to configureStore. Arrow functions are not usually needed here, but you can use them for defining reducers or middleware if required.

For example, if you want to define a simple reducer using an arrow function:
const myReducer = (state = {}, action) => {
  switch (action.type) {
    // ...handle actions...
    default:
      return state;
  }
}; 

const store = configureStore({
  reducer: {
    my: myReducer
  },
  devTools: process.env.NODE_ENV !== 'production',
});
export default store;
*/




/* 
在 React Redux 中， configureStore 和 createStore 是用于创建 Redux store 的两种不同方式，但它们分别属于不同的 Redux 版本和工具链。以下是它们的核心区别和推荐用法：

1. createStore （Redux 核心 API）
来源
来自 redux 核心库（ import { createStore } from 'redux' ）。
是 Redux 最原始的 store 创建方式。
特点
基础功能：仅支持同步 action 和 reducer。
中间件扩展：需手动集成中间件（如 redux-thunk ）：
javascript
Apply
import { applyMiddleware } from 'redux';
const store = createStore(reducer, applyMiddleware(thunk));
开发工具支持：需手动配置 Redux DevTools：
javascript
Apply
const store = createStore(
  reducer,
  window.__REDUX_DEVTOOLS_EXTENSION__?.()
);
适用场景
小型项目或需要完全控制 Redux 配置的场景。
兼容旧版 Redux 代码。
2. configureStore （Redux Toolkit 提供）
来源
来自 @reduxjs/toolkit （ import { configureStore } from '@reduxjs/toolkit' ）。
是 Redux 官方推荐的现代写法。
特点
开箱即用：
默认集成 redux-thunk （支持异步 action）。
自动启用 Redux DevTools。
内置不可变更新逻辑（基于 immer ）。
简化配置：
javascript
Apply
const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware(),
});
性能优化：
自动检测不可变数据更新。
支持代码分割和动态注入 reducer。
适用场景
新项目或需要快速开发的场景。
需要异步逻辑、性能优化或 TypeScript 支持的项目。
核心对比
特性	createStore	configureStore
来源	redux 核心库	@reduxjs/toolkit
异步支持	需手动添加中间件	默认集成 redux-thunk
DevTools	需手动配置	自动启用
不可变更新	需手动处理	内置 immer 支持
代码量	较多	极少
TypeScript 支持	需额外类型定义	完美支持
官方推荐
Redux 团队明确推荐使用 configureStore ，因为：
减少样板代码。
内置最佳实践（如不可变更新、DevTools）。
更好的 TypeScript 集成。
createStore 仍可用，但未来可能被标记为“遗留 API”。
迁移建议
如果现有项目使用 createStore ，可以逐步替换为 configureStore ：

安装 @reduxjs/toolkit ：
bash
Apply
npm install @reduxjs/toolkit
替换 createStore 为 configureStore ：
javascript
Apply
// 旧代码
import { createStore } from 'redux';
const store = createStore(reducer, applyMiddleware(thunk));

// 新代码
import { configureStore } from '@reduxjs/toolkit';
const store = configureStore({
  reducer: rootReducer,
});
总结
新项目：无条件选择 configureStore 。
旧项目：逐步迁移到 configureStore ，享受现代 Redux 的优势。
学习成本： configureStore 更简单，适合新手。
 */