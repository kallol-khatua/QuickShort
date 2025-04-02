import React, { useEffect } from "react";
import InviteTeammates from "../../components/dashboard/peoples/InviteTeammates";

const Invite: React.FC<{
  handleInviteModalToggle: () => void;
  isInviteModalOpen: boolean;
}> = ({ handleInviteModalToggle, isInviteModalOpen }) => {
  // Disable scrolling when modal is open
  useEffect(() => {
    if (isInviteModalOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isInviteModalOpen]);

  return (
    <div
      className={`min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 z-50`}
    >
      {/* Modal Overlay */}
      <div className="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-md z-50">
        <InviteTeammates
          handleInviteModalToggle={handleInviteModalToggle}
        />
      </div>
    </div>
  );
};

export default Invite;
