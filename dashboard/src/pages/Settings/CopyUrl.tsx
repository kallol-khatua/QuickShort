import React, { useEffect } from "react";
import CopyUrlModal from "../../components/dashboard/peoples/CopyUrlModal";

const CopyUrl: React.FC<{
  handleCopyInviteurlModalToggle: () => void;
  isCopyInviteUrlModalOpen: boolean;
}> = ({ handleCopyInviteurlModalToggle, isCopyInviteUrlModalOpen }) => {
  // Disable scrolling when modal is open
  useEffect(() => {
    if (isCopyInviteUrlModalOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isCopyInviteUrlModalOpen]);

  return (
    <div
      className={`min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 z-50`}
    >
      {/* Modal Overlay */}
      <div className="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-md z-50">
        <CopyUrlModal handleCopyInviteurlModalToggle={handleCopyInviteurlModalToggle} />
      </div>
    </div>
  );
};

export default CopyUrl;
