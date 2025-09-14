import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { Provider } from 'react-redux'
import { ThemeProvider } from '@emotion/react'
import CssBaseline from '@mui/material/CssBaseline'
import theme from './theme'
 import store from './store/reducer/store.js'
 

createRoot(document.getElementById('root')).render(

  <Provider store={store}> 




    <StrictMode>
      
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
    </StrictMode>

 </Provider>
 
)
