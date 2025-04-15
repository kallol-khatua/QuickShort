import { X } from "lucide-react";
import React from "react";
import { Plan } from "../../helper/Plan";

const ChoosePlanModal: React.FC<{
  handleChooseAnotherPlan: () => void;
  nextPlan: Plan;
  handlesetNextPlanId: (id: string) => void;
  currPlans: Plan[];
}> = ({
  handleChooseAnotherPlan,
  nextPlan,
  handlesetNextPlanId,
  currPlans,
}) => {
  const handlePlanChoose = (id: string) => {
    handlesetNextPlanId(id);
    handleChooseAnotherPlan();
  };
  return (
    <div
      className={`min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 z-50`}
    >
      {/* Modal Overlay */}
      <div className="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-md z-50">
        {/* Modal Content */}
        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-full sm:w-200 mx-3">
          {/* Modal Header */}
          <div className="flex justify-between items-center border-b pb-3">
            <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
              Choose next plan
            </h2>
            <button
              aria-label="n"
              onClick={handleChooseAnotherPlan}
              className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
            >
              <X size={20} />
            </button>
          </div>

          {/* Modal Body */}
          <div className="mt-3">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {currPlans.map((plans) => {
                return (
                  <div
                    key={plans.id}
                    className={`bg-white p-4 rounded shadow cursor-pointer rounded-lg dark:bg-white/[0.03] dark:text-white ${plans.id === nextPlan.id && "border border-blue-500"}`}
                    onClick={() => handlePlanChoose(plans.id)}
                  >
                    <div>Plan duration: {plans.planDurationMonth} months</div>
                    <div>Amount: {plans.amount}</div>
                    <div>Amount per month: {plans.amountPerMonth}</div>
                    {plans.planDuration !== "MONTHLY" && (
                      <div>Discount: {plans.percentageOff} %</div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Modal Footer */}
          <div className="mt-6 flex justify-end space-x-3">
            <button
              className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition"
              onClick={handleChooseAnotherPlan}
            >
              Close
            </button>

            {/* {loading ? (
              <button className="px-4 py-2 bg-blue-600 text-white rounded-md transition cursor-not-allowed">
                Submitting
              </button>
            ) : (
              <button
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
                onClick={handleCreateUrl}
              >
                Create
              </button>
            )} */}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChoosePlanModal;
