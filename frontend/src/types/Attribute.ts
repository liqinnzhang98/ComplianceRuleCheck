import { z } from 'zod';
import { AttributeType } from './AttributeType';
import { AttributeDataType } from './AttributeDataType';

export const Attribute = z.object({
    id: z.number(),
    name: z.string(),
    displayName: z.string(),
    type: z.nativeEnum(AttributeType),
    dataType: z.nativeEnum(AttributeDataType),
});

export type Attribute = z.infer<typeof Attribute>;

export type Schema = Attribute[];
