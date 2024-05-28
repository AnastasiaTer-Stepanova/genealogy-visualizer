import { FC, useRef } from 'react'
import { useGetGenealogyVisualizeGraph } from '../../hooks/hooks.tsx'
import { Graph } from './Graph.tsx'
import { ForceGraphMethods } from 'react-force-graph-3d'
import BasicAutocomplete from '../../common/Autocomplete/Autocomplete.tsx'
import { Link } from 'react-router-dom'
import { ROUTES } from '../../common/consts.ts'

export const GraphEntry: FC = () => {
    const { data, isLoading } = useGetGenealogyVisualizeGraph()
    const fgRef = useRef<ForceGraphMethods | null>(null)

    if (isLoading && !data) {
        return <div>Loading</div>
    }

    return (
        <div className="relative">
            <Graph data={data} fgRef={fgRef} />
            <BasicAutocomplete />
            <Link
                to={ROUTES.ABOUT}
                className="absolute bottom-10 left-1/2 -translate-x-1/2"
            >
                О ПРОЕКТЕ
            </Link>
        </div>
    )
}
