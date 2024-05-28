import { Link, Person } from './common.ts'

export interface GenealogyVisualizeGraphResponse {
    persons: Person[]
    links: Link[]
}
