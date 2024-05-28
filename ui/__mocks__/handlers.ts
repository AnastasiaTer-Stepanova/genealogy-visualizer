import { rest } from 'msw'
import { responseGraph } from './rest/graph-mock'

export const handlers = [
    rest.post('**/genealogy-visualizer/graph', (_req, res, ctx) => {
        return res(ctx.delay(1000), ctx.status(200), ctx.json(responseGraph))
    }),
]
