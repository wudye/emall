
import { ToastContainer } from 'react-toastify'
import './App.css'
import { BrowserRouter as Router, Routes, Route, createBrowserRouter, RouterProvider} from 'react-router-dom'
import Cart from "./components/cart/Cart";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import NavBar from "./components/shared/NavBar";
import Checkout from "./components/checkout/Checkout";
import About from "./components/About";
import Contact from "./components/Contact";
import Home from "./components/home/Home";
import Products from "./components/products/Products";
import { Outlet } from 'react-router-dom';
/* function App() {

  return (
    <div className='bg-amber-50'>
      <Router >
        <NavBar />
      <ToastContainer
          position="top-center"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
        />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Register />} />
          <Route path="/checkout" element={<Checkout />} />
        </Routes>
      </Router>
    </div>
  )
} */


const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <>
      <NavBar />
      <ToastContainer
          position="top-center"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
        />
        <Outlet />
      </>
    ),
    children: [
      { path: "/", element: <Home /> },
      { path: "/products", element: <Products /> },
      { path: "/about", element: <About /> },
      { path: "/contact", element: <Contact /> },
      { path: "/cart", element: <Cart /> },
      { path: "/login", element: <Login /> },
      { path: "/signup", element: <Register /> },
      { path: "/checkout", element: <Checkout /> },
    ],

  }
])
const App = () => {
  return (
    <div className='bg-amber-50'>
      <RouterProvider router={router} />
    </div>
  )

}


export default App
/* 

首页 ( <Home /> ) 放在 children 中是为了利用 React Router 的嵌套路由特性。以下是详细的解释：

1. 为什么将首页 ( <Home /> ) 放在 children 中？
嵌套路由的设计目的：
React Router 的嵌套路由允许你在父路由的布局（如 <NavBar /> + <Outlet /> ）中动态渲染子路由的内容。

父路由 ( path: "/" ) 定义了共享的布局（导航栏、Toast 通知等）。
子路由 ( children ) 定义了具体页面的内容（如首页、关于页等）。
根路径的匹配逻辑：

当访问 / 时，父路由和子路由的 path: "/" 会同时匹配，因此 <Home /> 会渲染到 <Outlet /> 的位置。
这种设计确保了所有页面（包括首页）都能共享父路由的布局（如 <NavBar /> ）。
2. 访问 /about 时的加载行为
不会加载 <Home /> ：
当访问 /about 时：

父路由的 element 会先加载（ <NavBar /> + <ToastContainer /> + <Outlet /> ）。
子路由中 path: "/about" 匹配成功， <About /> 会渲染到 <Outlet /> 的位置。
<Home /> 不会被加载，因为只有匹配的子路由才会渲染。
最终渲染结果：

html
Apply
<NavBar />
<ToastContainer />
<About />  <!-- 不是 <Home /> -->
3. 关键验证点
子路由的独立性：
每个子路由是互斥的，只有匹配当前 URL 的子路由才会渲染。

/ → <Home />
/about → <About />
/products → <Products />
共享布局的复用性：
<NavBar /> 和 <ToastContainer /> 是全局共享的，但子路由的内容不会互相干扰。

4. 是否需要调整？
当前设计是合理的：
这种结构是 React Router 的推荐实践，适合需要共享布局的场景。
如果不需要共享布局：
可以将所有路由平级定义（无嵌套），但会失去布局复用的优势。
5. 示例对比
(1) 当前嵌套路由结构
javascript
Apply
{
  path: "/",
  element: <SharedLayout />, // 包含 NavBar + Outlet
  children: [
    { path: "/", element: <Home /> }, // 首页
    { path: "/about", element: <About /> },
  ]
}
(2) 平级路由结构（无共享布局）
javascript
Apply
[
  { path: "/", element: <Home /> }, // 独立页面，无 NavBar
  { path: "/about", element: <About /> }, // 独立页面，无 NavBar
]
总结
访问 /about 时：只会加载 <NavBar /> + <About /> ，不会加载 <Home /> 。
嵌套路由的优势：复用布局，保持代码整洁。
无需修改：除非你希望首页或其他页面脱离共享布局。
如果需要进一步调整，请告诉我具体需求！ */