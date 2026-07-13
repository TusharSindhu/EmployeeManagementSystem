import { useCallback, useEffect, useRef, useState } from 'react'
import {
  createEmployee,
  deleteEmployee,
  getDepartments,
  getEmployees,
  getManagerOptions,
  updateEmployee,
} from './api/employeeApi'
import ConfirmDialog from './components/ConfirmDialog'
import EmployeeFilters from './components/EmployeeFilters'
import EmployeeForm from './components/EmployeeForm'
import EmployeeTable from './components/EmployeeTable'
import Pagination from './components/Pagination'
import { useDebounce } from './hooks/useDebounce'
import './App.css'

const emptyPage = {
  content: [],
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
  first: true,
  last: true,
}

const initialFilters = {
  search: '',
  department: '',
  managerId: '',
  joiningDateFrom: '',
  joiningDateTo: '',
}

function App() {
  const [employeePage, setEmployeePage] = useState(emptyPage)
  const [loading, setLoading] = useState(true)
  const [apiError, setApiError] = useState('')
  const [filters, setFilters] = useState(initialFilters)
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [sortBy, setSortBy] = useState('joiningDate')
  const [direction, setDirection] = useState('desc')
  const [departments, setDepartments] = useState([])
  const [managerOptions, setManagerOptions] = useState([])
  const [selectedEmployee, setSelectedEmployee] = useState(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [employeeToDelete, setEmployeeToDelete] = useState(null)
  const [saving, setSaving] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [formError, setFormError] = useState('')
  const loadSequence = useRef(0)
  const debouncedSearch = useDebounce(filters.search, 400)

  const loadEmployees = useCallback(async () => {
    const requestId = ++loadSequence.current
    setLoading(true)
    setApiError('')

    try {
      const response = await getEmployees({
        search: debouncedSearch,
        department: filters.department,
        managerId: filters.managerId,
        joiningDateFrom: filters.joiningDateFrom,
        joiningDateTo: filters.joiningDateTo,
        page,
        size: pageSize,
        sortBy,
        direction,
      })
      if (requestId === loadSequence.current) setEmployeePage(response)
      return response
    } catch (error) {
      if (requestId === loadSequence.current) setApiError(error.message)
      return null
    } finally {
      if (requestId === loadSequence.current) setLoading(false)
    }
  }, [debouncedSearch, direction, filters.department, filters.joiningDateFrom, filters.joiningDateTo, filters.managerId, page, pageSize, sortBy])

  useEffect(() => {
    loadEmployees()
  }, [loadEmployees])

  const loadDropdownOptions = useCallback(async () => {
    const [departmentData, managerData] = await Promise.all([getDepartments(), getManagerOptions()])
    setDepartments(departmentData)
    setManagerOptions(managerData)
  }, [])

  useEffect(() => {
    loadDropdownOptions().catch((error) => setApiError(error.message))
  }, [loadDropdownOptions])

  function changeFilter(name, value) {
    setFilters((current) => ({ ...current, [name]: value }))
    setPage(0)
  }

  function clearFilters() {
    setFilters(initialFilters)
    setPage(0)
  }

  function changeSort(field) {
    if (field === sortBy) {
      setDirection((current) => (current === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortBy(field)
      setDirection('asc')
    }
    setPage(0)
  }

  function openCreateForm() {
    setSelectedEmployee(null)
    setFormError('')
    setIsFormOpen(true)
  }

  function openEditForm(employee) {
    setSelectedEmployee(employee)
    setFormError('')
    setIsFormOpen(true)
  }

  function closeForm(force = false) {
    if (saving && !force) return
    setIsFormOpen(false)
    setSelectedEmployee(null)
    setFormError('')
  }

  async function saveEmployee(values) {
    setSaving(true)
    setFormError('')
    try {
      if (selectedEmployee) {
        await updateEmployee(selectedEmployee.id, values)
      } else {
        await createEmployee(values)
      }
      closeForm(true)
      await Promise.all([loadEmployees(), loadDropdownOptions()])
    } catch (error) {
      setFormError(error.message)
    } finally {
      setSaving(false)
    }
  }

  async function confirmDelete() {
    if (!employeeToDelete) return

    setDeleting(true)
    setApiError('')
    try {
      await deleteEmployee(employeeToDelete.id)
      setEmployeeToDelete(null)
      const refreshedPage = await loadEmployees()
      await loadDropdownOptions()
      if (refreshedPage && refreshedPage.content.length === 0 && refreshedPage.page > 0) {
        setPage((current) => current - 1)
      }
    } catch (error) {
      setApiError(error.message)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <main className={`app-shell ${isFormOpen ? 'form-open' : ''}`}>
      <section className="employee-management">
        <header className="page-header">
          <div>
            <p className="eyebrow">Employee management</p>
            <h1>Employees</h1>
          </div>
          <button type="button" className="button button-primary" onClick={openCreateForm}>+ Add employee</button>
        </header>

        <EmployeeFilters
          filters={filters}
          departments={departments}
          managerOptions={managerOptions}
          onChange={changeFilter}
          onClear={clearFilters}
        />

        {apiError && <p className="api-error" role="alert">{apiError}</p>}

        <EmployeeTable
          employees={employeePage.content}
          loading={loading}
          sortBy={sortBy}
          direction={direction}
          onSort={changeSort}
          onEdit={openEditForm}
          onDelete={setEmployeeToDelete}
        />

        <Pagination
          pageData={employeePage}
          onPageChange={setPage}
          onPageSizeChange={(size) => {
            setPageSize(size)
            setPage(0)
          }}
        />
      </section>

      {isFormOpen && (
        <EmployeeForm
          employee={selectedEmployee}
          departments={departments}
          managerOptions={managerOptions}
          onSubmit={saveEmployee}
          onClose={closeForm}
          saving={saving}
          submissionError={formError}
        />
      )}

      <ConfirmDialog
        employee={employeeToDelete}
        deleting={deleting}
        onCancel={() => !deleting && setEmployeeToDelete(null)}
        onConfirm={confirmDelete}
      />
    </main>
  )
}

export default App
