const sortableColumns = [
  { key: 'fullName', label: 'Employee name' },
  { key: 'employeeCode', label: 'Employee code' },
  { key: 'department', label: 'Department' },
  { key: 'joiningDate', label: 'Joining date' },
]

function EmployeeTable({ employees, loading, sortBy, direction, onSort, onEdit, onDelete }) {
  function sortIndicator(field) {
    if (sortBy !== field) return ''
    return direction === 'asc' ? ' ▲' : ' ▼'
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {sortableColumns.slice(0, 3).map((column) => (
              <th key={column.key} scope="col">
                <button type="button" className="sort-button" onClick={() => onSort(column.key)}>
                  {column.label}{sortIndicator(column.key)}
                </button>
              </th>
            ))}
            <th scope="col">Manager</th>
            <th scope="col">
              <button type="button" className="sort-button" onClick={() => onSort('joiningDate')}>
                Joining date{sortIndicator('joiningDate')}
              </button>
            </th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr><td colSpan="6" className="table-state">Loading employees…</td></tr>
          ) : employees.length === 0 ? (
            <tr><td colSpan="6" className="table-state">No employees found.</td></tr>
          ) : (
            employees.map((employee) => (
              <tr key={employee.id}>
                <td>{employee.fullName}</td>
                <td>{employee.employeeCode}</td>
                <td>{employee.department}</td>
                <td>{employee.manager ? employee.manager.fullName : '—'}</td>
                <td>{employee.joiningDate}</td>
                <td className="row-actions">
                  <button type="button" className="text-button" onClick={() => onEdit(employee)}>Edit</button>
                  <button type="button" className="text-button danger-text" onClick={() => onDelete(employee)}>Delete</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}

export default EmployeeTable
