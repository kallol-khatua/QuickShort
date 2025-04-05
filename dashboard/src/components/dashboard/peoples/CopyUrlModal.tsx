import React, { useEffect, useState } from "react";
import { X } from "lucide-react";
import { useSelector } from "react-redux";
import { RootState } from "../../../redux/store";
import toast from "react-hot-toast";

// type memberType = "MEMBER" | "OWNER";

const CopyUrlModal: React.FC<{
  handleCopyInviteurlModalToggle: () => void;
}> = ({ handleCopyInviteurlModalToggle }) => {
  const [url, setUrl] = useState("");
  const [memberType, setMemberType] = useState("MEMBER");

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const handleCopyUrl = async () => {
    await navigator.clipboard.writeText(url);

    toast.success("Invitation URL copied to clipboard!");
    handleCopyInviteurlModalToggle();
  };

  useEffect(() => {
    if (memberType === "MEMBER") {
      setUrl(
        `${import.meta.env.VITE_EMAIL_SERVICE_FONTEND}/${
          currentWorkspace?.workspaceId.id
        }/join-as-member`
      );
    } else {
      setUrl(
        `${import.meta.env.VITE_EMAIL_SERVICE_FONTEND}/${
          currentWorkspace?.workspaceId.id
        }/join-as-owner`
      );
    }
  }, [currentWorkspace?.workspaceId.id, memberType]);

  return (
    <>
      {/* Modal Content */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-96">
        {/* Modal Header */}
        <div className="flex justify-between items-center border-b pb-3">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
            Invite Teammates
          </h2>
          <button
            aria-label="n"
            onClick={handleCopyInviteurlModalToggle}
            className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div className="mt-2">
          <div className="flex flex-col space-y-1 w-full max-w-sm">
            {/* <label
              htmlFor="email"
              className="text-sm font-medium text-gray-700"
            >
              Email
            </label> */}

            <div className="flex items-center overflow-hidden gap-2 dark:text-white">
              <label htmlFor="memberType">Select member type</label>
              <select
                value={memberType}
                name="memberType"
                id="memberType"
                onChange={(e) => setMemberType(e.target.value)}
                className="px-3 py-2 text-sm text-gray-700 bg-white focus:outline-none dark:bg-gray-800 dark:text-white"
              >
                <option value="MEMBER">Member</option>
                <option value="OWNER">Owner</option>
              </select>
            </div>
          </div>
        </div>

        <div className="mt-2 dark:text-white">{url}</div>

        {/* Modal Footer */}
        <div className="mt-6 flex justify-end space-x-3">
          <button
            className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition"
            onClick={handleCopyInviteurlModalToggle}
          >
            Close
          </button>

          <button
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
            onClick={handleCopyUrl}
          >
            Copy URL
          </button>
        </div>
      </div>
    </>
  );
};

export default CopyUrlModal;
