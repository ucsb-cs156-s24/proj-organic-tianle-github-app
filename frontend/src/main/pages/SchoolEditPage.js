import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import SchoolForm from "main/components/School/SchoolForm";
import { Navigate } from 'react-router-dom'
import { useBackend, useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function SchoolEditPage({storybook=false}) {
  let { abbrev } = useParams();

  const { data: school, _error, _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      [`/api/schools?abbrev=${abbrev}`],
      {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
        method: "GET",
        url: `/api/schools/get`,
        params: {
          abbrev
        }
      }
    );


  const objectToAxiosPutParams = (school) => ({
    url: "/api/schools/update",
    method: "PUT",
    params: {
      abbrev: school.abbrev,
    },
    data: {
      name: school.name,
      termRegex: school.termRegex,
      termDescription: school.termDescription,
      termError: school.termError
    },
  });

  const onSuccess = (school) => {
    toast(`School Updated - abbrev: ${school.abbrev} name: ${school.name}`);
  }

  const mutation = useBackendMutation(
    objectToAxiosPutParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    [`/api/schools/update?abbrev=${abbrev}`]
  );

  const { isSuccess } = mutation

  const onSubmit = async (data) => {
    mutation.mutate(data);
  }

  if (isSuccess && !storybook) {
    return <Navigate to="/schools" />
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Edit School</h1>
        {
          school && <SchoolForm initialContents={school} submitAction={onSubmit} buttonLabel="Update" />
        }
      </div>
    </BasicLayout>
  )
}
