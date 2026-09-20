export type Quiz = {
    id: string,
    title: string,
    description: string,
    quizItems: QuizItem[]
}

export type QuizItem = {
    word: string,
    definition: string
}
