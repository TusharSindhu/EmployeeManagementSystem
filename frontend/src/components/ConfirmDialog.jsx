function ConfirmDialog({ employee, deleting, onCancel, onConfirm }) {
  if (!employee) return null

  return (
    <div className="dialog-backdrop" role="presentation">
      <section className="confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="delete-dialog-title">
        <h2 id="delete-dialog-title">Delete employee?</h2>
        <p>Delete {employee.fullName}? Direct reports will no longer have a manager.</p>
        <div className="form-actions">
          <button type="button" className="button button-secondary" onClick={onCancel} disabled={deleting}>Cancel</button>
          <button type="button" className="button button-danger" onClick={onConfirm} disabled={deleting}>
            {deleting ? 'Deleting…' : 'Delete employee'}
          </button>
        </div>
      </section>
    </div>
  )
}

export default ConfirmDialog
