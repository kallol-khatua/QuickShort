export interface Plan {
    id: string;
    workspaceType: "PRO" | "BUSINESS";
    memberLimit: number;
    linkCreationLimitPerMonth: number;
    amount: number;
    amountPerMonth: number;
    planDuration: "MONTHLY" | "HALF_YEARLY" | "QUARTERLY" | "YEARLY";
    planDurationMonth: number;
    percentageOff: number;
}

export const plans: Plan[] = [
    {
        id: "209537d6-ed71-43b8-86a9-bafdb87a4dc6",
        workspaceType: "PRO",
        memberLimit: 10,
        linkCreationLimitPerMonth: 1000,
        amount: 4800.0,
        amountPerMonth: 800.0,
        planDuration: "HALF_YEARLY",
        planDurationMonth: 6,
        percentageOff: 20.0,
    },
    {
        id: "30f33c4b-38d4-4572-9185-991535721c47",
        workspaceType: "PRO",
        memberLimit: 10,
        linkCreationLimitPerMonth: 1000,
        amount: 8400.0,
        amountPerMonth: 700.0,
        planDuration: "YEARLY",
        planDurationMonth: 12,
        percentageOff: 30.0,
    },
    {
        id: "62f181b2-9485-412d-ba29-f26d909ea4f0",
        workspaceType: "PRO",
        memberLimit: 10,
        linkCreationLimitPerMonth: 1000,
        amount: 2700.0,
        amountPerMonth: 900.0,
        planDuration: "QUARTERLY",
        planDurationMonth: 3,
        percentageOff: 10.0,
    },
    {
        id: "76fe2e81-9d29-4218-a05c-7ba9c2bc56a4",
        workspaceType: "BUSINESS",
        memberLimit: 25,
        linkCreationLimitPerMonth: 5000,
        amount: 21000.0,
        amountPerMonth: 1750.0,
        planDuration: "YEARLY",
        planDurationMonth: 12,
        percentageOff: 30.0,
    },
    {
        id: "a9a74a74-647b-4c5e-a18d-e609681f3626",
        workspaceType: "BUSINESS",
        memberLimit: 25,
        linkCreationLimitPerMonth: 5000,
        amount: 2500.0,
        amountPerMonth: 2500.0,
        planDuration: "MONTHLY",
        planDurationMonth: 1,
        percentageOff: 0.0,
    },
    {
        id: "b0a85146-8a79-4840-9a25-90bfcf918a4f",
        workspaceType: "PRO",
        memberLimit: 10,
        linkCreationLimitPerMonth: 1000,
        amount: 1000.0,
        amountPerMonth: 1000.0,
        planDuration: "MONTHLY",
        planDurationMonth: 1,
        percentageOff: 0.0,
    },
    {
        id: "ce4ff831-cd4d-4e77-a656-927fca472048",
        workspaceType: "BUSINESS",
        memberLimit: 25,
        linkCreationLimitPerMonth: 5000,
        amount: 12000.0,
        amountPerMonth: 2000.0,
        planDuration: "HALF_YEARLY",
        planDurationMonth: 6,
        percentageOff: 20.0,
    },
    {
        id: "f3feb902-9813-41fc-bb62-58620d0e3ab5",
        workspaceType: "BUSINESS",
        memberLimit: 25,
        linkCreationLimitPerMonth: 5000,
        amount: 6750.0,
        amountPerMonth: 2250.0,
        planDuration: "QUARTERLY",
        planDurationMonth: 3,
        percentageOff: 10.0,
    },
];