import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SchoolForm from "main/components/School/SchoolForm";
import { Navigate } from "react-router-dom";
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function SchoolCreatePage({ storybook = false }) {
  const objectToAxiosParams = (school) => ({
    url: "/api/schools/post",
    method: "POST",
    params: {
      abbrev: school.abbrev,
      name: school.name,
      termRegex: encodeURIComponent(school.termRegex),
      termDescription: school.termDescription,
      termError: school.termError,
    },
  });

  const onSuccess = (school) => {
    toast(`New school created - abbrev: ${school.abbrev}`);
  };

  const mutation = useBackendMutation(
    objectToAxiosParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    ["/api/schools/all"] // mutation makes this key stale so that pages relying on it reload
  );

  const { isSuccess } = mutation;

  const onSubmit = async (data) => {
    mutation.mutate(data);
  };

  if (isSuccess && !storybook) {
    return <Navigate to="/admin/schools" />;
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Create New School</h1>

        <SchoolForm submitAction={onSubmit} />
      </div>
    </BasicLayout>
  );
}
