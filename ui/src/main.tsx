import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { SWRConfig } from 'swr'

const enableMock = async () => {
    // TODO: enable mocks only in dev after back is ready
    // if (import.meta.env.DEV) {
    //     const { worker } = await import('../__mocks__/browser.ts')
    //
    //     worker.start()
    // }
    const { worker } = await import('../__mocks__/browser.ts')

    worker.start()
}

enableMock().then(() => {
    ReactDOM.createRoot(document.getElementById('root')!).render(
        <React.StrictMode>
            <SWRConfig value={{ provider: () => new Map() }}>
                <App />
            </SWRConfig>
        </React.StrictMode>
    )
})
