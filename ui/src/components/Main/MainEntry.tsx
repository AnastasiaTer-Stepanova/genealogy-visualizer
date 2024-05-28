import { FC } from 'react'
import SvgImage from '../../assets/search.svg'
import { Link } from 'react-router-dom'
import { ROUTES } from '../../common/consts.ts'

export const MainEntry: FC = () => {
    return (
        <div className="w-screen text-center">
            <div className="w-1/2 m-auto flex flex-col items-center">
                <SvgImage />
                <div className="text-gray-bg text-sm">Main</div>
                <div className="text-main text-base">Alergeya 400</div>
                <div className="font-medium text-gray-text text-lg">
                    Alergeya 500
                </div>
                <div className="font-medium text-link text-sm">
                    Евлампий Пантелеймонович Оводов 1788 г.р.
                </div>

                <Link to={ROUTES.ADMIN}>GO TO ADMIN</Link>
                <br />
                <Link to={ROUTES.GRAPH}>GO TO GRAPH</Link>
            </div>
        </div>
    )
}
