import { useEffect, useState } from 'react'

const emptyEmployee = {
  employeeCode: '',
  fullName: '',
  department: '',
  managerId: '',
  joiningDate: '',
  email: '',
  phoneNumber: '',
}

function valuesFromEmployee(employee) {
  if (!employee) return emptyEmployee

  return {
    employeeCode: employee.employeeCode,
    fullName: employee.fullName,
    department: employee.department,
    managerId: employee.manager ? String(employee.manager.id) : '',
    joiningDate: employee.joiningDate,
    email: employee.email,
    phoneNumber: employee.phoneNumber,
  }
}

function EmployeeForm({ employee, departments, managerOptions, onSubmit, onClose, saving, submissionError }) {
  const [values, setValues] = useState(emptyEmployee)
  const [fieldErrors, setFieldErrors] = useState({})

  useEffect(() => {
    setValues(valuesFromEmployee(employee))
    setFieldErrors({})
  }, [employee])

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }))
    setFieldErrors((current) => ({ ...current, [name]: '' }))
  }

  function validate() {
    const errors = {}
    for (const field of ['employeeCode', 'fullName', 'department', 'joiningDate', 'email', 'phoneNumber']) {
      if (!values[field].trim()) errors[field] = 'This field is required'
    }
    setFieldErrors(errors)
    return Object.keys(errors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()
    if (!validate()) return

    await onSubmit({
      ...values,
      managerId: values.managerId ? Number(values.managerId) : null,
    })
  }

  return (
    <aside className="employee-form-panel" aria-label={employee ? 'Edit employee' : 'Add employee'}>
      <div className="panel-heading">
        <h2>{employee ? 'Edit employee' : 'Add employee'}</h2>
        <button type="button" className="close-button" onClick={onClose} aria-label="Close form">×</button>
      </div>

      <form onSubmit={handleSubmit} noValidate>
        <FormField label="Full name" error={fieldErrors.fullName}>
          <input value={values.fullName} onChange={(event) => updateField('fullName', event.target.value)} />
        </FormField>
        <FormField label="Employee code" error={fieldErrors.employeeCode}>
          <input value={values.employeeCode} onChange={(event) => updateField('employeeCode', event.target.value)} />
        </FormField>
        <FormField label="Department" error={fieldErrors.department}>
          <select value={values.department} onChange={(event) => updateField('department', event.target.value)}>
            <option value="">Select department</option>
            {departments.map((department) => <option key={department} value={department}>{department}</option>)}
          </select>
        </FormField>
        <FormField label="Manager (optional)">
          <select value={values.managerId} onChange={(event) => updateField('managerId', event.target.value)}>
            <option value="">No manager</option>
            {managerOptions.filter((manager) => manager.id !== employee?.id).map((manager) => (
              <option key={manager.id} value={manager.id}>{manager.fullName} ({manager.employeeCode})</option>
            ))}
          </select>
        </FormField>
        <FormField label="Joining date" error={fieldErrors.joiningDate}>
          <input type="date" value={values.joiningDate} onChange={(event) => updateField('joiningDate', event.target.value)} />
        </FormField>
        <FormField label="Email" error={fieldErrors.email}>
          <input type="email" value={values.email} onChange={(event) => updateField('email', event.target.value)} />
        </FormField>
        <FormField label="Phone number" error={fieldErrors.phoneNumber}>
          <input value={values.phoneNumber} onChange={(event) => updateField('phoneNumber', event.target.value)} />
        </FormField>

        {submissionError && <p className="form-error" role="alert">{submissionError}</p>}

        <div className="form-actions">
          <button type="button" className="button button-secondary" onClick={onClose} disabled={saving}>Cancel</button>
          <button type="submit" className="button button-primary" disabled={saving}>
            {saving ? 'Saving…' : employee ? 'Save changes' : 'Save employee'}
          </button>
        </div>
      </form>
    </aside>
  )
}

function FormField({ label, error, children }) {
  return (
    <label className="form-field">
      <span>{label}</span>
      {children}
      {error && <small>{error}</small>}
    </label>
  )
}

export default EmployeeForm
