import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { GraphEntry } from './components/Graph/GraphEntry.tsx'
import { MainEntry } from './components/Main/MainEntry.tsx'
import { AdminEntry } from './components/Admin/AdminEntry.tsx'
import { ROUTES } from './common/consts.ts'
import { AboutEntry } from './components/About/AboutEntry.tsx'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path={ROUTES.MAIN} element={<MainEntry />} />
                <Route path={ROUTES.GRAPH} element={<GraphEntry />} />
                <Route path={ROUTES.ADMIN} element={<AdminEntry />} />
                <Route path={ROUTES.ABOUT} element={<AboutEntry />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
