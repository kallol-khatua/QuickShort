import React, { useState } from "react";
import { X } from "lucide-react";
import Input from "../../form/input/InputField";
import { useSelector } from "react-redux";
import { RootState } from "../../../redux/store";
import axios from "axios";
import toast from "react-hot-toast";

// type memberType = "MEMBER" | "OWNER";

const InviteTeammates: React.FC<{
  handleInviteModalToggle: () => void;
}> = ({ handleInviteModalToggle }) => {
  const [email, setEmail] = useState("");
  const [errorData, setErrorData] = useState<string>("");
  const [error, setError] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const [memberType, setMemberType] = useState("MEMBER");

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const handleCreateUrl = async () => {
    if (!email) {
      setErrorData("Enter a valid email address");
      setError(true);
      return;
    }

    setLoading(true);

    try {
      const response = await axios.post(
        `${import.meta.env.VITE_EMAIL_SERVICE_BASE_URL}/${
          currentWorkspace?.workspaceId.id
        }/add`,
        { email, memberType }
      );

      toast.success(response.data.message);
    } catch (err: unknown) {
      console.error("Unexpected error:", err);
      toast.error("Failed to send email")
    } finally {
      handleInviteModalToggle();
      setLoading(false);
    }
  };

  const handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void = (
    e
  ) => {
    setEmail(e.target.value);
    setErrorData("");
    setError(false);
  };

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
            onClick={handleInviteModalToggle}
            className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div className="mt-2">
          <div className="flex flex-col space-y-1 w-full max-w-sm">
            <label
              htmlFor="email"
              className="text-sm font-medium text-gray-700 dark:text-white"
            >
              Email
            </label>

            <div className="flex items-center overflow-hidden">
              <Input
                placeholder="john@example.com"
                name="email"
                type="email"
                value={email}
                onChange={(e) => {
                  handleInputChange(e);
                }}
                error={error}
              />
              <label htmlFor="memberType" className="sr-only">
                Select member type
              </label>
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

            {error && <p className="text-red-500 text-sm mt-1">{errorData}</p>}
          </div>
        </div>

        {error && <p>{error}</p>}

        {/* Modal Footer */}
        <div className="mt-6 flex justify-end space-x-3">
          <button
            className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition"
            onClick={handleInviteModalToggle}
          >
            Close
          </button>

          {loading ? (
            <button className="px-4 py-2 bg-blue-600 text-white rounded-md transition cursor-not-allowed">
              Sending
            </button>
          ) : (
            <button
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
              onClick={handleCreateUrl}
            >
              Send invite
            </button>
          )}
        </div>
      </div>
    </>
  );
};

export default InviteTeammates;
