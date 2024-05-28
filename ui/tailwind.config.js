/** @type {import('tailwindcss').Config} */
export default {
    content: [
        './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
        './src/components/**/*.{js,ts,jsx,tsx,mdx}',
        './src/app/**/*.{js,ts,jsx,tsx,mdx}',
    ],
    theme: {
        fontFamily: {
            alegreya: '"Alegreya Sans SC"',
        },
        fontSize: {
            sm: ['16px', '19px'],
            base: ['24px', '28px'],
            lg: ['32px', '38px'],
        },
        colors: {
            main: '#FFFFFF',
            link: 'rgba(93, 197, 255, 1)',
            'gray-text': 'rgba(255, 255, 255, 0.6)',
            'gray-bg': 'rgba(224, 244, 255, 0.2)',
            'gray-select-main': 'rgba(218, 235, 255, 0.2)',
            'gray-select-hover': 'rgba(218, 235, 255, 0.35)',
            'gray-select-active': 'rgba(218, 235, 255, 0.5)',
        },
        extend: {},
    },
    plugins: [],
}
