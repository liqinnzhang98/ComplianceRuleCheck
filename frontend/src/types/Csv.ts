import { z } from 'zod'; // zod is a checking data formate library

export const csvType = z.object({
    headers: z.string().array(),
    data: z.string().array(),
});

// generate a TypeScript type based on the EventLogUpload schema, and export it as EventLogUpload
export type csvType = z.infer<typeof csvType>;