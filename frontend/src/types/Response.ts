import { z } from 'zod';

const ResponseError = z.object({
    field: z.string(),
    description: z.string(),
});

type ResponseError = z.infer<typeof ResponseError>;

export default function createResponseSchema<DataType extends z.ZodTypeAny>(dataSchema: DataType) {
    return z.object({
        status: z.number(),
        message: z.string(),
        timestamp: z.string().datetime(),
        errorDetails: z.array(ResponseError).optional(),
        data: dataSchema.optional(),
    });
}
