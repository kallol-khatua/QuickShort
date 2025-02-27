export interface SuccessApiResponse {
    message: string;
    status: string;
    status_code: number;
    status_text: string;
    success: boolean;
    data?: object
}