import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface WorkspaceMember {
    id: string,
    memberType: string, // OWNER / MEMBER
    status: string,
    userId: {
        id: string,
        email: string,
        firstName: string,
        lastName: string,
        profileImageURL: string,
        role: string,
        status: string,
    },
    workspaceId: {
        id: string,
        name: string,
        type: string,
        linkCreationLimitPerMonth: number,
        createdLinksThisMonth: number,
        memberLimit: number,
        memberCount: number,
        lastResetDate: string,
        nextResetDate: string,
        nextBillingDate: string,
        workspaceStatus: string
    }
}

interface WorkspaceState {
    workspaces: WorkspaceMember[],
    currentWorkspace: WorkspaceMember | null,
    isLoaded: boolean
}

const initialState: WorkspaceState = {
    workspaces: [],
    currentWorkspace: null,
    isLoaded: false
};

const workspaceSlice = createSlice({
    name: "workspace",
    initialState,
    reducers: {
        setWorkspaces: (state, action: PayloadAction<WorkspaceMember[]>) => {
            state.workspaces = action.payload;
        },
        setCurrentWorkspace: (state, action: PayloadAction<WorkspaceMember | null>) => {
            state.currentWorkspace = action.payload;
        },
        setIsLoaded: (state, action: PayloadAction<boolean>) => {
            state.isLoaded = action.payload;
        }
    },
});

export const { setWorkspaces, setCurrentWorkspace, setIsLoaded } = workspaceSlice.actions;
export default workspaceSlice.reducer;
