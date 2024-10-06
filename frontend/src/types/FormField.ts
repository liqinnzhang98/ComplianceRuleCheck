import { z } from 'zod';
import { AttributeType } from './AttributeType';

export enum FormFieldType {
    TEXT = 'TEXT',
    COLUMN_BREAK = 'COLUMN_BREAK',
    DATA = 'DATA',
    NUMBER = 'NUMBER',
    CHECK = 'CHECK',
    SELECT = 'SELECT',
    TIME = 'TIME',
    DURATION = 'DURATION',
}

export const FormField = z.object({
    name: z.string(),
    type: z.nativeEnum(FormFieldType),
    label: z.string().optional(),
    text: z.string().optional(),
    selectFrom: z.nativeEnum(AttributeType).optional(),
    dependsOn: z.string().optional(),
    options: z.array(z.string()).optional(),
});

export type FormField = z.infer<typeof FormField>;
