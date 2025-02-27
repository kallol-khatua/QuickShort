export interface ErrorApiResponse {
    message: string;
    status: string;
    status_code: number;
    status_text: string;
    success: boolean;
    errors: { message: string, field?: string }[]
}