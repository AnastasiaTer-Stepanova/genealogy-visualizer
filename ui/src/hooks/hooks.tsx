import useSWR, { SWRResponse } from 'swr'
import { GenealogyVisualizeGraphResponse } from '../types/genealogy.ts'
import { SWR_KEYS } from './keys.ts'

export function useGetGenealogyVisualizeGraph<T = undefined>(
    requestData?: T
): SWRResponse<GenealogyVisualizeGraphResponse> {
    return useSWR(
        SWR_KEYS.GRAPH,
        (url) =>
            fetch(url, {
                method: 'POST',
                body: JSON.stringify(requestData),
            }).then((res) => res.json()),
        {
            revalidateOnFocus: false,
        }
    )
}
