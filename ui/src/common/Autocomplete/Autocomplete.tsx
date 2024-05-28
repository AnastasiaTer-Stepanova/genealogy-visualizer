import { Autocomplete } from '@mui/joy'
import { useGetGenealogyVisualizeGraph } from '../../hooks/hooks.tsx'
import Search from '../../assets/search.svg'
import Close from '../../assets/close.svg'
import { Person } from '../../types/common.ts'
import './override.css'

const getOptionKey = (option: Person | string) => {
    const opt = option as Person
    return opt.id
}

const getOptionLabel = (option: Person | string) => {
    const opt = option as Person
    return opt.fullName.name
}

const selectStyle = {
    paddingLeft: '0.75rem',
    width: '40%',
    height: '48px',
    position: 'absolute',
    top: '32px',
    left: '50%',
    transform: 'translateX(-50%)',
    background: 'rgba(218, 235, 255, 0.2)',
    // backdropFilter: 'blur(10px)',
    color: 'rgba(255, 255, 255, 1)',
    fontFamily: 'Alegreya Sans SC',
    ':hover': {
        background: 'rgba(218, 235, 255, 0.35)',
        color: 'rgba(255, 255, 255, 1)',
    },
    '::placeholder': {
        color: 'rgba(255, 255, 255, 0)',
    },
    ':focus-within': {
        background: 'rgba(218, 235, 255, 0.5)',
        color: 'rgba(255, 255, 255, 1)',
    },
    '--Input-focusedThickness': 'transparent',
}

export default function BasicAutocomplete() {
    const { data } = useGetGenealogyVisualizeGraph()

    if (!data) {
        return null
    }

    return (
        <>
            <Autocomplete
                placeholder="Поиск"
                freeSolo
                startDecorator={<Search />}
                clearIcon={<Close />}
                getOptionKey={getOptionKey}
                options={data.persons}
                getOptionLabel={getOptionLabel}
                variant="plain"
                sx={selectStyle}
            />
        </>
    )
}
