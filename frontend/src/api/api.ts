import axios, { AxiosHeaders } from 'axios';
import type { AxiosRequestConfig } from 'axios';
import { z } from 'zod';
// import type { z } from 'zod';
import createResponseSchema from '../types/Response';

export enum HTTPMethod {
    GET = 'GET',
    POST = 'POST',
    PUT = 'PUT',
    DELETE = 'DELETE',
}

export enum HTTPStatusCode {
    OK = 200,
}

export default function api<Request, Response>({
    method,
    path,
    requestSchema,
    responseSchema,
    headers,
}: {
    method: HTTPMethod;
    path: string;
    responseSchema: z.ZodType<Response>;
    requestSchema?: z.ZodType<Request> | null;
    headers?: AxiosHeaders | undefined | null;
}): (data: Request) => Promise<Response> {
    return function (requestData: Request) {
        async function apiCall() {
            // check to make sure request data matches schema
            if (requestSchema) {
                const requestResult = requestSchema.safeParse(requestData);
                if (!requestResult.success) {
                    throw new Error(JSON.stringify(requestResult.error.issues));
                }
            }

            const axiosConfig: AxiosRequestConfig = {
                method,
                url: '/api' + path,
                [method === HTTPMethod.GET ? 'params' : 'data']: requestData,
            };

            if (headers) {
                axiosConfig.headers = headers;
            }

            // perform actual AJAX query
            const response = await axios(axiosConfig);

            // check to make sure response matches schema
            const responseResult = createResponseSchema(responseSchema).safeParse(response.data);
            if (!responseResult.success) {
                throw new Error(JSON.stringify(responseResult.error.issues));
            }
            if (responseResult.data.status != HTTPStatusCode.OK || responseResult.data.data == undefined) {
                throw new Error(JSON.stringify(responseResult.data.errorDetails));
            }

            // return data otherwise
            return responseResult.data.data;
        }

        return apiCall();
    };
}
