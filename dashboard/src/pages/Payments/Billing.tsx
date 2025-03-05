import PageBreadcrumb from "../../components/common/PageBreadCrumb";

const ListInvitations: React.FC = () => {
  return (
    <div className="dark:bg-gray-900 rounded-lg">
      <div
        // key={invitation.id}
        className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
      >
        <div className="sm:flex sm:justify-between sm:items-center">
          {/* title */}
          <div className="flex items-center space-x-2">
            <div>
              {/* <Avatar src={invitation.userId.profileImageURL} size="medium" /> */}
            </div>

            <div className="flex flex-col">
              <div className="font-semibold dark:text-white">
                sokdhsj
              </div>
              <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                psodjf
              </p>
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
              // onClick={() => handleVerifyInvitation(invitation)}
            >
              Allow
            </button>
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
              // onClick={() => handleRejectInvitation(invitation)}
            >
              Reject
            </button>
          </div>
        </div>

        {/* triple dot icon */}
      </div>
      <div
        // key={invitation.id}
        className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3"
      >
        <div className="sm:flex sm:justify-between sm:items-center">
          {/* title */}
          <div className="flex items-center space-x-2">
            <div>
              {/* <Avatar src={invitation.userId.profileImageURL} size="medium" /> */}
            </div>

            <div className="flex flex-col">
              <div className="font-semibold dark:text-white">
                sokdhsj
              </div>
              <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                psodjf
              </p>
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-center gap-5 mt-2 sm:mt-0">
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-success-500 text-white dark:text-white shadow-theme-xs hover:bg-success-600`}
              // onClick={() => handleVerifyInvitation(invitation)}
            >
              Allow
            </button>
            <button
              className={`inline-flex items-center justify-center gap-2 rounded-lg transition font-medium px-4 py-3 text-sm bg-error-500 text-white dark:text-white shadow-theme-xs hover:bg-error-600`}
              // onClick={() => handleRejectInvitation(invitation)}
            >
              Reject
            </button>
          </div>
        </div>

        {/* triple dot icon */}
      </div>
    </div>
  );
};

const Billing = () => {
  return (
    <div className="min-h-full flex flex-col">
      <PageBreadcrumb pageTitle="Billing" />

      <ListInvitations />
    </div>
  );
};

export default Billing;
