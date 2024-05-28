// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import {
    CSS2DRenderer,
    CSS2DObject,
} from 'three/addons/renderers/CSS2DRenderer.js'
import { FC, useCallback } from 'react'
import ForceGraph3D, { ForceGraphMethods } from 'react-force-graph-3d'
import { GenealogyVisualizeGraphResponse } from '../../types/genealogy.ts'

interface GraphProps {
    data?: GenealogyVisualizeGraphResponse
    fgRef: React.MutableRefObject<ForceGraphMethods | null>
}

const extraRenderers = [new CSS2DRenderer()]

export const Graph: FC<GraphProps> = ({ data, fgRef }) => {
    const handleClick = useCallback(
        (node) => {
            // Aim at node from outside it
            const distance = 40
            const distRatio = 1 + distance / Math.hypot(node.x, node.y, node.z)

            if (fgRef?.current) {
                fgRef.current.cameraPosition(
                    {
                        x: node.x * distRatio,
                        y: node.y * distRatio,
                        z: node.z * distRatio,
                    }, // new position
                    node, // lookAt ({ x, y, z })
                    3000 // ms transition duration
                )
            }
        },
        [fgRef]
    )

    return (
        <ForceGraph3D
            extraRenderers={extraRenderers}
            ref={fgRef}
            nodeResolution={100}
            enableNodeDrag={false}
            graphData={{ nodes: data?.persons, links: data?.links }}
            nodeAutoColorBy="group"
            nodeLabel="first_name"
            onNodeClick={handleClick}
            nodeThreeObjectExtend={true}
            nodeThreeObject={(node) => {
                const nodeEl = document.createElement('div')
                nodeEl.textContent = node.fullName.name
                nodeEl.style.color = node.color
                nodeEl.className = 'node-label'
                nodeEl.dataset.id = node.fullName.name
                nodeEl.dataset.node = JSON.stringify(node)
                return new CSS2DObject(nodeEl)
            }}
        />
    )
}
