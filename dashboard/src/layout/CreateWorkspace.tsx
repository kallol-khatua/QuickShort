import React, { useEffect } from "react";
import CreateWorkspaceComponent from "../components/dashboard/sidebar/CreateWorkspaceComponent";

const CreateWorkspace: React.FC<{
  handleWorksapceCreateModalToggle: () => void;
  isCreateWorkspaceModalOpen: boolean;
}> = ({ handleWorksapceCreateModalToggle, isCreateWorkspaceModalOpen }) => {
  // Disable scrolling when modal is open
  useEffect(() => {
    if (isCreateWorkspaceModalOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isCreateWorkspaceModalOpen]);

  return (
    <div
    className={`min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 z-50`}
    >
      <div className="fixed inset-0 w-full flex  items-center justify-center bg-black/50 backdrop-blur-md z-50">
        <CreateWorkspaceComponent
          handleWorksapceCreateModalToggle={handleWorksapceCreateModalToggle}
        />
      </div>
    </div>
  );
};

export default CreateWorkspace;
