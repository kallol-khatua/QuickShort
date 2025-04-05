import { useEffect, useState } from "react";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import { logout } from "../../redux/authSlice";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import axiosWorkspaceInstance from "../../axiosWorkspaceInstance";
import RotatingLoader from "../../components/ui/loader/RotatingLoader";
import { Link2 } from "lucide-react";
import { SuccessApiResponse } from "../../helper/SuccessApiResponse";
import Avatar from "../../components/ui/avatar/Avatar";
import { FiMoreVertical } from "react-icons/fi";
import toast from "react-hot-toast";
import Invite from "./Invite";
import CopyUrl from "./CopyUrl";

interface Users {
  id: string;
  email: string;
  profileImageURL: string | null;
  role: string;
}

type Response = {
  userId: Users;
  id: string;
  memberType: string;
  status: string;
};

interface MemberResponseData extends SuccessApiResponse {
  data: Response[];
}

const ListMembers: React.FC<{ members: Response[] }> = ({ members }) => {
  return (
    <div className="dark:bg-gray-900 rounded-lg">
      {members.map((member) => (
        <div
          key={member.id}
          className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3 flex justify-between items-center"
        >
          <div className="flex items-center">
            <div>
              <div className="flex items-center space-x-2">
                <div>
                  <Avatar src={member.userId.profileImageURL} size="medium" />
                </div>

                <div className="flex flex-col">
                  <div className="font-semibold dark:text-white">
                    {member.userId.email}
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                    {member.memberType}
                  </p>
                </div>

                {/* <FaCopy
                  className="text-gray-500 dark:text-gray-400 cursor-pointer"
                  onClick={() => handleLinkCopy(link.shortCode)}
                /> */}
              </div>
            </div>
          </div>

          {/* triple dot icon */}
          <div className="text-gray-500 dark:text-gray-400 flex items-center">
            <FiMoreVertical className="cursor-pointer ml-3" />
          </div>
        </div>
      ))}
    </div>
  );
};

const ListInvitations: React.FC<{
  invitations: Response[];
  handleReload: () => void;
}> = ({ invitations, handleReload }) => {
  const dispatch = useDispatch();
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const handleVerifyInvitation = async (invitation: Response) => {
    try {
      await axiosWorkspaceInstance.post(
        `/${currentWorkspace?.workspaceId.id}/verify-member/${invitation.id}/verify`
      );

      handleReload();
      toast.success("Successfully verified");
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401 || errorData.status_code === 403) {
          dispatch(logout());
        }
      } else {
        console.error("Unexpected error:", err);
      }
    }
  };

  const handleRejectInvitation = async (invitation: Response) => {
    try {
      await axiosWorkspaceInstance.post(
        `/${currentWorkspace?.workspaceId.id}/verify-member/${invitation.id}/reject`
      );

      handleReload();
      toast.success("Successfully verified");
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401 || errorData.status_code === 403) {
          dispatch(logout());
        }
      } else {
        console.error("Unexpected error:", err);
      }
    }
  };

  return (
    <div className="dark:bg-gray-900 rounded-lg">
      {invitations.length > 0 ? (
        invitations.map((invitation) => (
          <div
            key={invitation.id}
            className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
          >
            <div className="sm:flex sm:justify-between sm:items-center">
              {/* title */}
              <div className="flex items-center space-x-2">
                <div>
                  <Avatar
                    src={invitation.userId.profileImageURL}
                    size="medium"
                  />
                </div>

                <div className="flex flex-col">
                  <div className="font-semibold dark:text-white">
                    {invitation.userId.email}
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                    {invitation.memberType}
                  </p>
                </div>
              </div>

              {/* Action buttons */}
              <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
                <button
                  className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
                  onClick={() => handleVerifyInvitation(invitation)}
                >
                  Allow
                </button>
                <button
                  className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
                  onClick={() => handleRejectInvitation(invitation)}
                >
                  Reject
                </button>
              </div>
            </div>

            {/* triple dot icon */}
          </div>
        ))
      ) : (
        <div className="flex justify-center items-center">
          <p className="mt-1 text-gray-600 text-theme-md dark:text-gray-500">
            No Invitations
          </p>
        </div>
      )}
    </div>
  );
};

const People: React.FC = () => {
  const dispatch = useDispatch();

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [reload, setReload] = useState<boolean>(false);
  const [members, setMembers] = useState<Response[]>([]);
  const [invitations, setInvitations] = useState<Response[]>([]);
  const [allreaponse, setAllreaponse] = useState<Response[]>([]);
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);
  const [isCopyInviteUrlModalOpen, setISCopyInviteUrlModalOpen] =
    useState(false);

  // filter member where status = verified
  useEffect(() => {
    const filteredMembers = allreaponse.filter((member) => {
      return member.status === "VERIFIED";
    });

    setMembers(filteredMembers);
  }, [allreaponse]);

  // filter memebrs where status = applied
  useEffect(() => {
    const filteredInvitations = allreaponse.filter((member) => {
      return member.status === "APPLIED";
    });

    setInvitations(filteredInvitations);
  }, [allreaponse]);

  const handleReload = () => {
    setReload((prev) => !prev);
  };

  const handleInviteModalToggle = () => {
    setIsInviteModalOpen((prev) => !prev);
  };

  const handleCopyInviteurlModalToggle = () => {
    setISCopyInviteUrlModalOpen((prev) => !prev);
  };

  // Load members
  useEffect(() => {
    const members = async () => {
      try {
        const response = await axiosWorkspaceInstance.get<MemberResponseData>(
          `/${currentWorkspace?.workspaceId.id}/members`
        );

        setAllreaponse(response.data.data);
        // console.log(response.data.data);

        setReload(false);
        setTimeout(() => {
          setIsLoaded(true);
        }, 250);
      } catch (err: unknown) {
        if (axios.isAxiosError(err) && err.response) {
          const errorData: ErrorApiResponse = err.response.data;

          // if unauthorized the logout using auth slice, protected route will take to signin page
          if (errorData.status_code === 401) {
            dispatch(logout());
          }
        } else {
          console.error("Unexpected error:", err);
        }
      }
    };

    members();
  }, [currentWorkspace?.workspaceId.id, dispatch, reload]);

  const [type, setType] = useState("Members");

  // console.log(type)

  return (
    <div className="min-h-full flex flex-col">
      <PageBreadcrumb pageTitle="People" />

      <div className="flex flex-col flex-1 min-h-0">
        {/* Search and Filter Section */}
        <div className="w-full">
          <div className="flex justify-between mb-2">
            <p className="mt-1 text-gray-500 text-theme-sm dark:text-gray-400">
              Teammates that have access to this workspace.
            </p>

            <div className="flex space-x-2 items-center">
              {/* Invite Button */}
              <button
                className="flex items-center justify-center font-medium text-white rounded-lg bg-gray-900 text-theme-sm hover:bg-gray-800 dark:bg-white dark:text-black dark:bg-gray-300 h-10 px-4"
                onClick={handleInviteModalToggle}
              >
                Invite
              </button>
              {/* Link Button */}
              <button
                className="border border-black dark:border-white h-10 px-4 py-2 rounded-md flex items-center justify-center transition"
                onClick={handleCopyInviteurlModalToggle}
              >
                <Link2 size={20} className="text-black dark:text-white" />
              </button>
            </div>
          </div>

          <div className="flex">
            <div className="flex items-center bg-gray-200 dark:bg-gray-700 p-1 rounded-full mb-6 ">
              {/* Members */}
              <button
                onClick={() => setType("Members")}
                className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                  type === "Members"
                    ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                    : "text-gray-600 dark:text-gray-300"
                }`}
              >
                Members
              </button>

              {/* Invitations */}
              <button
                onClick={() => setType("Invitations")}
                className={`px-4 py-2 text-sm font-medium rounded-full transition ${
                  type === "Invitations"
                    ? "bg-white dark:bg-gray-900 text-gray-900 dark:text-white shadow-md"
                    : "text-gray-600 dark:text-gray-300"
                }`}
              >
                Invitations
              </button>
            </div>
          </div>
        </div>

        {isLoaded ? (
          type === "Members" ? (
            <ListMembers members={members} />
          ) : (
            <ListInvitations
              invitations={invitations}
              handleReload={handleReload}
            />
          )
        ) : (
          <div className="flex-1 min-h-0 flex justify-center items-center w-full">
            <div className="w-full">
              <RotatingLoader />
            </div>
          </div>
        )}
      </div>

      {isInviteModalOpen && (
        <Invite
          isInviteModalOpen={isInviteModalOpen}
          handleInviteModalToggle={handleInviteModalToggle}
        />
      )}

      {isCopyInviteUrlModalOpen && (
        <CopyUrl
          isCopyInviteUrlModalOpen={isCopyInviteUrlModalOpen}
          handleCopyInviteurlModalToggle={handleCopyInviteurlModalToggle}
        />
      )}
    </div>
  );
};

export default People;
