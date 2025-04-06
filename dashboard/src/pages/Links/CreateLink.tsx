import React, { useEffect } from "react";
import CreateLinkComponent from "../../components/dashboard/links/CreateLinkComponent";

const CreateLink: React.FC<{
  handleLinkCreateModalToggle: () => void;
  isCreateLinkModalOpen: boolean;
  handleLinkReload: () => void
}> = ({ handleLinkCreateModalToggle, isCreateLinkModalOpen, handleLinkReload }) => {
  // Disable scrolling when modal is open
  useEffect(() => {
    if (isCreateLinkModalOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isCreateLinkModalOpen]);

  return (
    <div
      className={`min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 z-50`}
    >
      {/* Modal Overlay */}
      <div className="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-md z-50">
        <CreateLinkComponent
          handleLinkCreateModalToggle={handleLinkCreateModalToggle}
          handleLinkReload={handleLinkReload}
        />
      </div>
    </div>
  );
};

export default CreateLink;
