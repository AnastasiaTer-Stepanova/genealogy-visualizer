export interface FullName {
    name: string
    surname: string
    lastName: string
    statuses: string[]
}

export enum DateRangeType {
    EXACTLY = 'ровно',
    BEFORE = 'до',
    AFTER = 'после',
}

export interface DateInfo {
    date: string
    dateRangeType: DateRangeType
}

export enum SEX {
    MALE = 'мужской',
    FEMALE = 'женский',
}

export interface Person {
    fullName: FullName
    birthDate: DateInfo
    deathDate: DateInfo
    sex: SEX
    id: number
}

export interface Link {
    source: number
    target: number
}
